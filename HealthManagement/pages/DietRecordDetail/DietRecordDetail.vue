<template>
	<view class="dietDetail">
		<view class="header card">
			<view class="row">
				<view class="label">餐别</view>
				<picker @change="onMealTypeChange" :value="mealTypeIndex" :range="mealTypes" v-if="!record.id">
					<view class="picker-value">{{record.mealType}}</view>
				</picker>
				<view class="value" v-else>{{record.mealType}}</view>
			</view>
			<view class="row">
				<view class="label">时间</view>
				<view class="value">{{formatDateTime(record.recordedAt)}}</view>
			</view>
			<view class="row">
				<view class="label">计划热量</view>
				<view class="value">{{record.planCalories}} kcal</view>
			</view>
			<view class="row">
				<view class="label">备注</view>
				<view class="value">{{record.note || '无'}}</view>
			</view>
		</view>

		<view class="card">
			<view class="section_title">食物明细</view>
			<view class="items">
				<view class="item" v-for="item in items" :key="item.id">
					<view class="left">
						<view class="name">{{item.foodName}}</view>
						<view class="sub">{{item.quantity}} {{item.unit}}</view>
					</view>
					<view class="right">
						<view class="kcal">{{item.calories}} kcal</view>
					</view>
				</view>
				<view v-if="items.length===0" class="empty">暂无明细</view>
			</view>
		</view>

		<view class="card total">
			<view>总热量</view>
			<view class="total_kcal">{{totalKcal}} kcal</view>
		</view>

		<view class="footer">
			<button class="btn_secondary" @click="goBack">返回</button>
			<button class="btn_primary" @click="saveDietRecord" v-if="!record.id">保存记录</button>
		</view>
	</view>
</template>

<script>
export default {
	data(){
		return {
			record: {
				id: null,
				mealType: '早餐',
				recordedAt: '',
				planCalories: 600,
				note: ''
			},
			items: [],
			totalKcal: 0,
			mealTypes: ['早餐', '午餐', '晚饭', '加餐'],
			mealTypeIndex: 0
		};
	},
	onLoad(query){
		console.log('饮食记录详情页面加载，参数:', query);
		// 从上一页传参或从后端获取数据
		if (query.recordId) {
			console.log('加载饮食记录详情，recordId:', query.recordId);
			this.loadDietRecord(query.recordId);
		} else if (query.recipeId) {
			// 从食谱页面传来的参数
			console.log('加载食谱数据，recipeId:', query.recipeId);
			this.loadRecipeData(query.recipeId, query.recipeName);
		} else {
			// 默认显示当前时间
			console.log('没有参数，显示新建页面');
			this.record.recordedAt = this.getNow();
		}
	},
	methods:{
		getNow(){
			const d = new Date();
			const p = (n)=> String(n).padStart(2,'0');
			return `${d.getFullYear()}-${p(d.getMonth()+1)}-${p(d.getDate())}T${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}.000Z`;
		},
		formatDateTime(dateTimeStr) {
			if (!dateTimeStr) return '';
			const date = new Date(dateTimeStr);
			const p = (n)=> String(n).padStart(2,'0');
			return `${date.getFullYear()}-${p(date.getMonth()+1)}-${p(date.getDate())} ${p(date.getHours())}:${p(date.getMinutes())}`;
		},
		// 加载食谱数据
		loadRecipeData(recipeId, recipeName) {
			// 设置基本信息
			this.record.recordedAt = this.getNow();
			this.record.note = `使用食谱：${decodeURIComponent(recipeName || '')}`;
			
			// 加载食谱详情
			uni.request({
				url: `http://localhost:8088/api/recipes/${recipeId}/detail`,
				method: 'GET',
				success: (res) => {
					if (res.data.code === 0) {
						const data = res.data.data;
						this.record.note = `使用食谱：${data.recipe.name}`;
						
						// 将食谱明细转换为饮食记录明细
						this.items = data.items.map(item => ({
							id: item.id,
							foodId: item.foodId,
							recipeId: null, // 食谱明细不关联食谱ID
							foodName: item.foodName,
							quantity: item.quantity,
							unit: item.quantityUnit,
							calories: 0 // 初始为0，稍后计算
						}));
						
						// 计算每个食物的热量
						this.calculateItemCalories();
					} else {
						uni.showToast({
							title: '加载食谱失败',
							icon: 'none'
						});
					}
				},
				fail: () => {
					uni.showToast({
						title: '网络错误',
						icon: 'none'
					});
				}
			});
		},
		
		loadDietRecord(recordId) {
			console.log('开始加载饮食记录详情，recordId:', recordId);
			// 从后端获取饮食记录详情
			const token = uni.getStorageSync('token');
			if (!token) {
				uni.showToast({ title: '请先登录', icon: 'none' });
				return;
			}
			
			uni.request({
				url: `http://localhost:8088/api/diet-records/${recordId}`,
				method: 'GET',
				header: {
					'Authorization': 'Bearer ' + token
				},
				success: (res) => {
					console.log('饮食记录详情API响应:', res);
					if (res.data.code === 0) {
						this.record = res.data.data;
						console.log('设置记录数据:', this.record);
						// 获取饮食记录明细
						this.loadDietRecordItems(recordId);
					} else {
						console.log('获取记录失败:', res.data.message);
						uni.showToast({ title: '获取记录失败', icon: 'none' });
					}
				},
				fail: (err) => {
					console.log('网络错误:', err);
					uni.showToast({ title: '网络错误', icon: 'none' });
				}
			});
		},
		loadDietRecordItems(recordId) {
			console.log('开始加载饮食记录明细，recordId:', recordId);
			// 从后端获取饮食记录明细
			const token = uni.getStorageSync('token');
			
			uni.request({
				url: `http://localhost:8088/api/diet-records/${recordId}/items`,
				method: 'GET',
				header: {
					'Authorization': 'Bearer ' + token
				},
				success: (res) => {
					console.log('饮食记录明细API响应:', res);
					if (res.data.code === 0) {
						this.items = res.data.data || [];
						console.log('设置明细数据:', this.items);
						this.recalcTotal();
					} else {
						console.log('获取明细失败:', res.data.message);
					}
				},
				fail: (err) => {
					console.log('获取明细网络错误:', err);
				}
			});
		},
		// 餐别选择
		onMealTypeChange(e) {
			this.mealTypeIndex = e.detail.value;
			this.record.mealType = this.mealTypes[e.detail.value];
		},
		
		// 计算食物热量
		calculateItemCalories() {
			// 为每个食物项计算热量
			this.items.forEach((item, index) => {
				// 这里需要根据食物ID获取营养信息来计算热量
				// 暂时使用估算值，实际应该调用API获取食物营养信息
				this.calculateSingleItemCalories(item, index);
			});
		},
		
		// 计算单个食物项的热量
		calculateSingleItemCalories(item, index) {
			// 根据食物名称和数量估算热量
			// 这里使用简单的估算公式，实际应该从后端获取准确的营养数据
			let caloriesPer100g = 0;
			
			// 根据食物名称估算每100g的热量
			const foodName = item.foodName.toLowerCase();
			if (foodName.includes('鸡胸肉') || foodName.includes('鸡肉')) {
				caloriesPer100g = 165;
			} else if (foodName.includes('牛肉')) {
				caloriesPer100g = 250;
			} else if (foodName.includes('三文鱼') || foodName.includes('鲈鱼')) {
				caloriesPer100g = 208;
			} else if (foodName.includes('鸡蛋')) {
				caloriesPer100g = 155;
			} else if (foodName.includes('牛奶')) {
				caloriesPer100g = 42;
			} else if (foodName.includes('燕麦')) {
				caloriesPer100g = 389;
			} else if (foodName.includes('米饭')) {
				caloriesPer100g = 130;
			} else if (foodName.includes('蔬菜') || foodName.includes('生菜') || foodName.includes('西兰花')) {
				caloriesPer100g = 25;
			} else if (foodName.includes('番茄')) {
				caloriesPer100g = 18;
			} else if (foodName.includes('豆腐')) {
				caloriesPer100g = 76;
			} else if (foodName.includes('酸奶')) {
				caloriesPer100g = 59;
			} else if (foodName.includes('水果') || foodName.includes('草莓') || foodName.includes('蓝莓')) {
				caloriesPer100g = 50;
			} else {
				caloriesPer100g = 100; // 默认值
			}
			
			// 计算总热量
			let quantity = parseFloat(item.quantity) || 0;
			let calories = (caloriesPer100g * quantity) / 100;
			
			// 更新热量值
			this.$set(this.items, index, {
				...item,
				calories: Math.round(calories)
			});
			
			// 重新计算总热量
			this.recalcTotal();
		},
		
		recalcTotal(){
			this.totalKcal = this.items.reduce((s, i) => s + (Number(i.calories) || 0), 0);
		},
		// 保存饮食记录
		saveDietRecord() {
			const token = uni.getStorageSync('token');
			if (!token) {
				uni.showToast({ title: '请先登录', icon: 'none' });
				return;
			}
			
			// 构建饮食记录数据
			const dietRecord = {
				mealType: this.record.mealType,
				recordedAt: this.record.recordedAt,
				planCalories: this.record.planCalories,
				note: this.record.note,
				items: this.items.map(item => ({
					foodId: item.foodId || null, // 食物ID，如果没有则为null
					recipeId: item.recipeId || null, // 食谱ID，如果没有则为null
					quantity: item.quantity,
					unit: item.unit,
					calories: item.calories
				}))
			};
			
			uni.request({
				url: 'http://localhost:8088/api/diet-records',
				method: 'POST',
				header: {
					'Authorization': 'Bearer ' + token,
					'Content-Type': 'application/json'
				},
				data: dietRecord,
				success: (res) => {
					if (res.data.code === 0) {
						uni.showToast({
							title: '保存成功',
							icon: 'success'
						});
						// 保存成功后返回上一页
						setTimeout(() => {
							uni.navigateBack();
						}, 1500);
					} else {
						uni.showToast({
							title: res.data.message || '保存失败',
							icon: 'none'
						});
					}
				},
				fail: () => {
					uni.showToast({
						title: '网络错误',
						icon: 'none'
					});
				}
			});
		},
		goBack() {
			uni.navigateBack();
		}
	}
}
</script>

<style scoped>
.dietDetail {
	padding: 20px;
	background: #f5f5f5;
	min-height: 100vh;
}

.card {
	background: #fff;
	border-radius: 12px;
	padding: 20px;
	margin-bottom: 20px;
	box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.header .row {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 12px 0;
	border-bottom: 1px solid #f0f0f0;
}

.header .row:last-child {
	border-bottom: none;
}

.label {
	font-size: 16px;
	color: #666;
	font-weight: 500;
}

.value {
	font-size: 16px;
	color: #333;
	font-weight: 600;
}

.picker-value {
	font-size: 16px;
	color: #333;
	font-weight: 600;
	padding: 8px 0;
}

.section_title {
	font-size: 18px;
	font-weight: 600;
	color: #333;
	margin-bottom: 16px;
}

.items {
	margin-bottom: 16px;
}

.item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 12px 0;
	border-bottom: 1px solid #f0f0f0;
}

.item:last-child {
	border-bottom: none;
}

.left .name {
	font-size: 16px;
	color: #333;
	font-weight: 500;
	margin-bottom: 4px;
}

.left .sub {
	font-size: 14px;
	color: #666;
}

.right .kcal {
	font-size: 16px;
	color: #ff6b35;
	font-weight: 600;
}

.empty {
	text-align: center;
	color: #999;
	padding: 40px 0;
	font-size: 14px;
}

.total {
	display: flex;
	justify-content: space-between;
	align-items: center;
	font-size: 18px;
	font-weight: 600;
	color: #333;
}

.total_kcal {
	color: #ff6b35;
	font-size: 20px;
}

.footer {
	padding: 20px 0;
	display: flex;
	gap: 12px;
}

.btn_secondary {
	flex: 1;
	height: 48px;
	background: #f0f0f0;
	color: #666;
	border: none;
	border-radius: 8px;
	font-size: 16px;
	font-weight: 500;
}

.btn_primary {
	flex: 1;
	height: 48px;
	background: #4A89FF;
	color: #fff;
	border: none;
	border-radius: 8px;
	font-size: 16px;
	font-weight: 500;
}
</style>