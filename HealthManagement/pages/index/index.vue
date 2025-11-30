<template>

	<view class="roomElement">

		<view class="overview_box card_box">

			<view class="charts-box">

				<qiun-data-charts type="arcbar" :opts="opts" :chartData="chartData" />

			</view>

			<view class="information_box">

				<view class="stat_item">

					<view class="stat_title">

						<text class="dot" style="color:var(--danger-color);">●</text>活动热量

					</view>

					<view class="stat_content">

						<text class="value">{{exerciseStats.totalCalories}}</text>/{{exerciseStats.targetCalories}} 千卡

					</view>

				</view>

				<view class="stat_item">

					<view class="stat_title">

						<text class="dot" style="color:var(--warning-color);">●</text>锻炼时长

					</view>

					<view class="stat_content">

						<text class="value">{{exerciseStats.totalDuration}}</text>/{{exerciseStats.targetDuration}} 分钟

					</view>

				</view>

				<view class="stat_item">

					<view class="stat_title">

						<text class="dot" style="color:var(--main-color);">●</text>每日步数

					</view>

					<view class="stat_content">

						<text class="value">{{exerciseStats.totalSteps}}</text>/{{exerciseStats.targetSteps}} 步

					</view>

				</view>

			</view>

		</view>

		<view class="diet_box card_box">

			<view class="diet_title_bar">

				<view class="section_title">饮食日记</view>

				<view class="diet_actions">

					<button class="diet_btn" @click="openDietModal">记录饮食</button>

					<button class="diet_btn" @click="goToRecipes"
						style="margin-left: 8px; background: #4A89FF;">食谱推荐</button>

				</view>

			</view>

			<view class="diet_list">

				<view class="diet_record list_item" v-for="di in diet_infos" :key="di.type" @click="openDietDetail(di)">

					<view class="record_ico">

						<image :src="di.icon" />

					</view>

					<view class="diet_content">

						<view class="diet_type">

							{{di.type}}

						</view>

						<view class="diet_info">

							<text class="value">{{di.diet_num}}</text> 份 | <text class="value">{{di.diet_kcal}}</text> /
							<text>{{di.diet_plan_kcal}}</text> kcal

						</view>

					</view>

					<view class="diet_right">

						...

					</view>

				</view>

			</view>

		</view>



		<view v-if="showDietModal" class="modal_mask" @click="closeDietModal"></view>

		<view v-if="showDietModal" class="modal_container">

			<view class="modal_title">记录饮食</view>

			<view class="form_row">

				<view class="form_label">餐别</view>

				<picker mode="selector" :range="mealTypes" @change="onMealTypeChange">

					<view class="picker_value">{{dietForm.type}}</view>

				</picker>

			</view>

			<view class="form_row">

				<view class="form_label">从食物库</view>

				<button class="diet_btn" @click.stop="goSelectFood">选择食物</button>

			</view>

			<view v-if="dietForm.food_name" class="form_row">

				<view class="form_label">已选食物</view>

				<view class="selected_food">{{dietForm.food_name}}</view>

			</view>

			<view class="form_row">

				<view class="form_label">份数</view>

				<input class="form_input" type="number" v-model.number="dietForm.diet_num" placeholder="请输入份数" />

			</view>

			<view class="form_row">

				<view class="form_label">热量(kcal)</view>

				<input class="form_input" type="number" v-model.number="dietForm.diet_kcal" placeholder="本次摄入热量" />

			</view>

			<view class="form_row">

				<view class="form_label">计划(kcal)</view>

				<input class="form_input" type="number" v-model.number="dietForm.diet_plan_kcal" placeholder="当日计划热量" />

			</view>

			<view class="modal_actions">

				<button class="btn_cancel" @click="closeDietModal">取消</button>

				<button class="btn_confirm" @click="submitDietRecord">保存</button>

			</view>

		</view>

	</view>

</template>



<script>
	import qiunDataCharts from '@/uni_modules/qiun-data-charts/components/qiun-data-charts/qiun-data-charts.vue';



	export default {

		components: {
			qiunDataCharts
		},

		data() {

			return {

				showDietModal: false,

				mealTypes: ['早餐', '午餐', '晚饭', '加餐'],

				dietForm: {

					icon: '/static/fried_egg.png',

					type: '早餐',

					food_name: '', // 添加食物名称字段

					diet_num: 1,

					diet_kcal: 0,

					diet_plan_kcal: 600

				},

				chartData: {

					series: [

						{

							name: "活动小时数",

							data: 0.8, // 14 / 12 = 1.16

						},

						{

							name: "锻炼时长",

							data: 1, // 76 / 25 = 3.04

						},

						{

							name: "活动热量",

							data: 0.8, // 538 / 516 = 1.04

						}

					]

				},

				opts: {

					color: ["#F5296E", "#FFCC00", "#4A89FF"],

					padding: undefined,

					title: {

						name: "",

						fontSize: 35,

						color: "#1890ff"

					},

					subtitle: {

						name: "",

						fontSize: 15,

						color: "#666666"

					},

					extra: {

						arcbar: {

							type: "circle",

							width: 18,

							backgroundColor: "#E9E9E9",

							startAngle: 1.5,

							endAngle: 0.25,

							gap: 1.5

						}

					}

				},

				diet_infos: [

					{

						icon: '/static/fried_egg.png',

						type: '早餐',

						diet_num: 2,

						diet_kcal: 316,

						diet_plan_kcal: 600

					},

					{

						icon: '/static/lunch.png',

						type: '午餐',

						diet_num: 2,

						diet_kcal: 316,

						diet_plan_kcal: 600

					},

					{

						icon: '/static/dinner.png',

						type: '晚饭',

						diet_num: 2,

						diet_kcal: 316,

						diet_plan_kcal: 600

					},

					{

						icon: '/static/extra_meal.png',

						type: '加餐',

						diet_num: 2,

						diet_kcal: 316,

						diet_plan_kcal: 600

					}

				],

				// 运动统计数据

				exerciseStats: {

					totalCalories: 0,

					targetCalories: 500,

					totalDuration: 0,

					targetDuration: 60,

					totalSteps: 0,

					targetSteps: 10000

				},

				userInfo: {

					dailyCalorieGoal: 600,

					dailyExerciseGoal: 30,

					dailyStepGoal: 10000

				}

			};

		},

		onLoad() {

			this.loadUserInfo(); // 先加载用户信息

			this.loadDietRecords();

			this.loadExerciseData();


			// 监听运动数据刷新事件

			uni.$on('refreshExerciseData', () => {

				this.loadExerciseData();

			});

		},

		onShow() {

			// 页面显示时刷新运动数据

			this.loadExerciseData();

		},

		onUnload() {

			// 页面卸载时移除事件监听

			uni.$off('refreshExerciseData');

		},

		methods: {

			// 加载用户信息

			loadUserInfo() {

				const token = uni.getStorageSync('token');

				const storedUserInfo = uni.getStorageSync('userInfo');


				if (storedUserInfo) {

					this.userInfo = {
						...this.userInfo,
						...storedUserInfo
					};

				}


				if (!token) {

					console.log('未登录，使用默认用户信息');

					return;

				}


				// 从后端获取用户信息

				uni.request({

					url: 'http://localhost:8088/api/users/me',

					method: 'GET',

					header: {

						'Authorization': 'Bearer ' + token

					},

					success: (res) => {

						if (res.data.code === 0) {

							const userData = res.data.data;

							this.userInfo = {

								...this.userInfo,

								...userData

							};

							// 保存到本地存储

							uni.setStorageSync('userInfo', this.userInfo);

						}

					},

					fail: () => {

						console.log('加载用户信息失败，使用本地数据');

					}

				});

			},


			// 加载饮食记录

			loadDietRecords() {

				const token = uni.getStorageSync('token');

				if (!token) {

					uni.showToast({

						title: '请先登录',

						icon: 'none'

					});

					return;

				}


				uni.request({

					url: 'http://localhost:8088/api/diet-records',

					method: 'GET',

					header: {

						'Authorization': 'Bearer ' + token

					},

					success: (res) => {

						if (res.data.code === 0) {

							// 将后端数据转换为前端显示格式

							const pageData = res.data.data;

							const records = pageData.records || [];

							this.diet_infos = records.map(record => ({

								id: record.id, // 添加记录ID

								icon: this.getMealIcon(record.mealType),

								type: record.mealType,

								diet_num: 1, // 默认值，实际应该从record.items计算

								diet_kcal: record.planCalories || 0,

								diet_plan_kcal: record.planCalories || 600

							}));

						}

					},

					fail: () => {

						console.log('加载饮食记录失败，使用本地数据');

					}

				});

			},


			// 加载运动数据

			loadExerciseData() {

				const token = uni.getStorageSync('token');

				if (!token) {

					console.log('未登录，跳过运动数据加载');

					return;

				}


				uni.request({

					url: 'http://localhost:8088/api/exercise-records',

					method: 'GET',

					header: {

						'Authorization': 'Bearer ' + token

					},

					success: (res) => {

						if (res.data.code === 0) {

							const records = res.data.data.records || [];

							this.updateChartData(records);

						}

					},

					fail: () => {

						console.log('加载运动数据失败，使用默认数据');

					}

				});

			},


			// 更新图表数据

			updateChartData(exerciseRecords) {

				// 计算今日运动数据

				const today = new Date().toISOString().split('T')[0];

				const todayRecords = exerciseRecords.filter(record => {

					const recordDate = new Date(record.exerciseTime).toISOString().split('T')[0];

					return recordDate === today;

				});


				// 计算统计数据

				let totalDuration = 0; // 总锻炼时长（分钟）

				let totalCalories = 0; // 总消耗卡路里

				let totalSteps = 0; // 总步数（模拟计算）


				todayRecords.forEach(record => {

					totalDuration += record.duration || 0;

					totalCalories += record.calories || 0;

					// 根据运动类型和时长估算步数

					totalSteps += this.estimateSteps(record.exerciseType || record.exerciseName, record.duration ||
						0);

				});


				// 使用用户设置的目标值

				const targetSteps = this.userInfo.dailyStepGoal || 10000; // 目标步数

				const targetDuration = this.userInfo.dailyExerciseGoal || 30; // 目标锻炼时长（分钟）

				const targetCalories = this.userInfo.dailyCalorieGoal || 600; // 目标消耗卡路里


				// 计算完成度（0-1之间，最大为1）

				const stepsProgress = Math.min(totalSteps / targetSteps, 1);

				const durationProgress = Math.min(totalDuration / targetDuration, 1);

				const caloriesProgress = Math.min(totalCalories / targetCalories, 1);


				// 更新图表数据

				this.chartData = {

					series: [

						{

							name: "每日步数",

							data: stepsProgress,

						},

						{

							name: "锻炼时长",

							data: durationProgress,

						},

						{

							name: "活动热量",

							data: caloriesProgress,

						}

					]

				};


				// 更新统计信息数据

				this.exerciseStats = {

					totalCalories: Math.round(totalCalories),

					targetCalories: targetCalories,

					totalDuration: Math.round(totalDuration),

					targetDuration: targetDuration,

					totalSteps: Math.round(totalSteps),

					targetSteps: targetSteps

				};


				console.log('图表数据已更新:', {

					totalSteps: totalSteps,

					totalDuration: totalDuration,

					totalCalories: totalCalories,

					stepsProgress: stepsProgress.toFixed(2),

					durationProgress: durationProgress.toFixed(2),

					caloriesProgress: caloriesProgress.toFixed(2)

				});

			},


			// 根据运动类型和时长估算步数

			estimateSteps(exerciseType, duration) {

				// 不同运动类型的步数估算（每分钟）

				const stepsPerMinute = {

					'跑步': 150, // 跑步每分钟约150步

					'游泳': 0, // 游泳不计步数

					'骑行': 0, // 骑行不计步数

					'健身': 20, // 健身每分钟约20步

					'瑜伽': 10, // 瑜伽每分钟约10步

					'篮球': 120, // 篮球每分钟约120步

					'足球': 130, // 足球每分钟约130步

					'羽毛球': 80, // 羽毛球每分钟约80步

					'乒乓球': 30, // 乒乓球每分钟约30步

					'其他': 50 // 其他运动每分钟约50步

				};


				const stepsPerMin = stepsPerMinute[exerciseType] || 50;

				return Math.round(stepsPerMin * duration);

			},


			// 获取餐别图标

			getMealIcon(mealType) {

				const iconMap = {

					'早餐': '/static/fried_egg.png',

					'午餐': '/static/lunch.png',

					'晚饭': '/static/dinner.png',

					'加餐': '/static/extra_meal.png'

				};

				return iconMap[mealType] || '/static/fried_egg.png';

			},


			// 刷新运动数据（供其他页面调用）

			refreshExerciseData() {

				this.loadExerciseData();

			},


			openDietModal() {

				this.showDietModal = true;

			},

			closeDietModal() {

				this.showDietModal = false;

			},

			onMealTypeChange(e) {

				const index = e.detail.value;

				this.dietForm.type = this.mealTypes[index];

				const iconMap = {

					'早餐': '/static/fried_egg.png',

					'午餐': '/static/lunch.png',

					'晚饭': '/static/dinner.png',

					'加餐': '/static/extra_meal.png'

				};

				this.dietForm.icon = iconMap[this.dietForm.type] || '/static/fried_egg.png';

			},

			goSelectFood() {

				uni.navigateTo({

					url: '/pages/FoodSearch/FoodSearch',

					events: {

						// none

					},

					success: (res) => {

						res.eventChannel.on('foodSelected', (food) => {

							// 回填热量（默认按100g，用户可再调整份数）

							this.dietForm.food_name = food.name;

							this.dietForm.diet_kcal = food.calories_per_100;

							uni.showToast({
								title: '已选择：' + food.name,
								icon: 'none'
							});

						});

					}

				});

			},

			goToRecipes() {

				uni.navigateTo({

					url: '/pages/Recipes/Recipes'

				});

			},

			openDietDetail(di) {

				// 如果有记录ID，传递过去；否则创建新记录

				if (di.id) {

					uni.navigateTo({

						url: `/pages/DietRecordDetail/DietRecordDetail?recordId=${di.id}`

					});

				} else {

					uni.navigateTo({

						url: '/pages/DietRecordDetail/DietRecordDetail'

					});

				}

			},

			submitDietRecord() {

				const n = Number(this.dietForm.diet_num);

				const kcal = Number(this.dietForm.diet_kcal);

				const plan = Number(this.dietForm.diet_plan_kcal);

				if (!this.dietForm.type || !n || n <= 0 || isNaN(kcal) || kcal <= 0) {

					uni.showToast({
						title: '请填写有效信息',
						icon: 'none'
					});

					return;

				}


				// 调用后端API保存饮食记录

				const dietRecord = {

					mealType: this.dietForm.type,

					recordedAt: new Date().toISOString(),

					planCalories: plan > 0 ? plan : 600,

					note: ''

				};


				// 调试：打印发送的数据

				console.log('发送的饮食记录数据:', JSON.stringify(dietRecord, null, 2));

				console.log('dietForm数据:', JSON.stringify(this.dietForm, null, 2));


				// 获取token

				const token = uni.getStorageSync('token')


				uni.request({

					url: 'http://localhost:8088/api/diet-records',

					method: 'POST',

					data: dietRecord,

					header: {

						'Authorization': token ? 'Bearer ' + token : ''

					},

					success: (res) => {

						if (res.data.code === 0) {

							// 更新本地显示

							this.diet_infos.unshift({

								icon: this.dietForm.icon,

								type: this.dietForm.type,

								diet_num: n,

								diet_kcal: kcal,

								diet_plan_kcal: plan > 0 ? plan : 600

							});

							this.closeDietModal();

							uni.showToast({
								title: '已记录',
								icon: 'success'
							});

						} else {

							uni.showToast({
								title: '保存失败',
								icon: 'none'
							});

						}

					},

					fail: () => {

						// 如果API调用失败，仍然更新本地显示

						this.diet_infos.unshift({

							icon: this.dietForm.icon,

							type: this.dietForm.type,

							diet_num: n,

							diet_kcal: kcal,

							diet_plan_kcal: plan > 0 ? plan : 600

						});

						this.closeDietModal();

						uni.showToast({
							title: '已记录(离线)',
							icon: 'success'
						});

					}

				});

			}

		}

	};
</script>



<style scoped>
	/* 定义颜色变量 */

	:root {

		--main-color: #4A89FF;

		--warning-color: #FFCC00;

		--danger-color: #F5296E;

		--background-color: #f7f8f9;

		--card-background: #ffffff;

		--text-color-primary: #333333;

		--text-color-secondary: #666666;

		--border-color: #ebebeb;

		--box-shadow: 0px 2px 8px rgba(99, 99, 99, 0.1);

	}



	.roomElement {

		width: 100%;

		min-height: 100vh;

		background-color: var(--background-color);

		padding-bottom: 3vh;

	}



	.card_box {

		width: 95%;

		margin: 15px auto;

		background-color: var(--card-background);

		border-radius: 12px;

		box-shadow: var(--box-shadow);

		padding: 20px;

		box-sizing: border-box;

	}


	.overview_box {

		height: auto;
		/* 使用 auto 适应内容 */

		padding-top: 20px;

		border-radius: 0 0 12px 12px;

		margin-top: 0;

	}



	.charts-box {

		width: 100%;

		height: 250px;
		/* 固定高度确保图表显示 */

	}



	.information_box {

		display: flex;

		justify-content: space-around;

		text-align: center;

		margin-top: 20px;

		padding-top: 20px;

		border-top: 1px dashed var(--border-color);

	}



	.stat_item {

		width: 33%;

		text-align: left;

	}



	.stat_title {

		font-size: 14px;

		color: var(--text-color-secondary);

		display: flex;

		align-items: center;

	}



	.dot {

		font-size: 24px;

		margin-right: 5px;

	}



	.stat_content {

		font-size: 14px;

		padding-top: 5px;

		color: var(--text-color-secondary);

	}



	.stat_content .value {

		font-size: 18px;

		font-weight: bold;

		color: var(--text-color-primary);

	}



	.section_title {

		font-size: 18px;

		font-weight: bold;

		color: var(--text-color-primary);

	}



	.diet_box {

		padding: 20px;

	}



	.diet_title_bar {

		display: flex;

		align-items: center;

		justify-content: space-between;

		padding-bottom: 15px;

		border-bottom: 1px solid var(--border-color);

		margin-bottom: 15px;

	}



	.diet_actions {
		display: flex;
		gap: 10px; /* 添加按钮间距 */
	}


	.diet_btn {

		background-color: var(--danger-color);

		color: #fff;

		border: none;

		padding: 8px 16px;

		border-radius: 20px;

		font-size: 14px;

		line-height: 1;

		height: auto;

		min-width: 80px;

		text-align: center;

	}



	.diet_list {

		/* 列表容器 */

	}



	.list_item {

		display: flex;

		align-items: center;

		padding: 15px 0;

		border-bottom: 1px solid var(--border-color);

	}


	.list_item:last-child {

		border-bottom: none;

	}



	.record_ico {

		width: 40px;

		height: 40px;

		border-radius: 50%;

		overflow: hidden;

		margin-right: 15px;

		flex-shrink: 0;

	}



	.record_ico image {

		width: 100%;

		height: 100%;

	}



	.diet_content {

		flex: 1;

	}



	.diet_type {

		font-weight: bold;

		font-size: 16px;

		color: var(--text-color-primary);

	}



	.diet_info {

		color: var(--text-color-secondary);

		font-size: 14px;

		margin-top: 5px;

	}



	.diet_info .value {

		font-weight: bold;

		color: var(--text-color-primary);

	}



	.diet_right {

		font-weight: bold;

		font-size: 18px;

		color: #ccc;

		padding-left: 15px;

	}



	/* 弹窗样式优化 */

	.modal_mask {

		position: fixed;

		left: 0;

		top: 0;

		right: 0;

		bottom: 0;

		background: rgba(0, 0, 0, 0.5);

		z-index: 999;

	}



	.modal_container {

		position: fixed;

		left: 50%;

		top: 50%;

		transform: translate(-50%, -50%);

		width: 85vw;

		max-width: 350px;

		background: var(--card-background);

		border-radius: 12px;

		padding: 24px;

		box-shadow: var(--box-shadow);

		z-index: 1000;

	}



	.modal_title {

		font-size: 20px;

		font-weight: bold;

		text-align: center;

		margin-bottom: 24px;

		color: var(--text-color-primary);

	}



	.form_row {

		display: flex;

		align-items: center;

		margin-bottom: 16px;

	}



	.form_label {

		color: var(--text-color-secondary);

		font-size: 14px;

		width: 30%;

		flex-shrink: 0;

	}



	.form_input,

	.picker_value {

		flex: 1;

		height: 44px;

		line-height: 44px;

		background: var(--background-color);

		border-radius: 8px;

		padding: 0 12px;

		font-size: 14px;

		border: 1px solid var(--border-color);

		box-sizing: border-box;

	}



	.form_input::placeholder {

		color: #999;

	}


	.picker_value {

		color: var(--text-color-primary);

	}



	.modal_actions {

		display: flex;

		justify-content: space-between;

		gap: 12px;

		margin-top: 24px;

	}



	.btn_cancel,

	.btn_confirm {

		flex: 1;

		height: 44px;

		line-height: 44px;

		border: none;

		border-radius: 8px;

		font-size: 16px;

		text-align: center;

		min-width: 0;
		/* Ensures flex works properly */

	}



	.btn_cancel {

		background: #eee;

		color: var(--text-color-primary);

	}



	.btn_confirm {

		background: var(--danger-color);

		color: #fff;

	}


	.selected_food {

		flex: 1;

		height: 44px;

		line-height: 44px;

		background: #f0f8ff;

		border-radius: 8px;

		padding: 0 12px;

		font-size: 14px;

		color: var(--main-color);

		border: 1px solid #e6f3ff;

		box-sizing: border-box;

	}
</style>