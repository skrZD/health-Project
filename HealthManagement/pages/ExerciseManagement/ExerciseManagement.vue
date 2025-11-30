<template>
	<view class="exercisePage">
		<view class="header_box">
			<view class="page_title">
				运动记录
			</view>
			<view class="add_exercise_btn" @click="showAddModal = true">
				<text>+ 添加运动</text>
			</view>
		</view>
		
		<view class="summary_box card_box">
			<view class="summary_title">
				今日运动概览
			</view>
			<view class="summary_stats">
				<view class="stat_item">
					<view class="stat_value">{{todayStats.duration}}</view>
					<view class="stat_label">运动时长(分钟)</view>
				</view>
				<view class="stat_item">
					<view class="stat_value">{{todayStats.calories}}</view>
					<view class="stat_label">消耗卡路里</view>
				</view>
				<view class="stat_item">
					<view class="stat_value">{{todayStats.count}}</view>
					<view class="stat_label">运动次数</view>
				</view>
			</view>
		</view>
		
		<view class="exercise_list_box card_box">
			<view class="list_title">
				运动记录
			</view>
			<view class="exercise_list">
				<view class="exercise_item" v-for="exercise in exerciseList" :key="exercise.id">
					<view class="exercise_icon">
						<text class="icon_emoji">{{exercise.icon}}</text>
					</view>
					<view class="exercise_info">
						<view class="exercise_name">{{exercise.name}}</view>
						<view class="exercise_details">
							<text class="detail_value">{{exercise.duration}}</text> 分钟 | 
							<text class="detail_value">{{exercise.calories}}</text> 千卡 | 
							{{exercise.time}}
						</view>
					</view>
					<view class="exercise_actions">
						<text class="edit_btn" @click.stop="editExercise(exercise)">编辑</text>
						<text class="delete_btn" @click.stop="deleteExercise(exercise.id)">删除</text>
					</view>
				</view>
			</view>
		</view>
		
		<view class="modal" v-if="showAddModal">
			<view class="modal_content">
				<view class="modal_header">
					<text class="modal_title">{{isEdit ? '编辑运动' : '添加运动'}}</text>
					<text class="close_btn" @click="closeModal">×</text>
				</view>
				<view class="modal_body">
					<view class="form_item">
						<text class="form_label">运动类型</text>
						<picker @change="onExerciseTypeChange" :value="exerciseTypeIndex" :range="exerciseTypes">
							<view class="picker_input">
								{{exerciseTypes[exerciseTypeIndex]}}
							</view>
						</picker>
					</view>
					<view class="form_item">
						<text class="form_label">运动时长(分钟)</text>
						<input type="number" v-model="formData.duration" placeholder="请输入运动时长"/>
					</view>
					<view class="form_item">
						<text class="form_label">消耗卡路里</text>
						<input type="number" v-model="formData.calories" placeholder="请输入消耗卡路里"/>
					</view>
					<view class="form_item">
						<text class="form_label">运动时间</text>
						<picker mode="time" @change="onTimeChange" :value="formData.time">
							<view class="picker_input">
								{{formData.time}}
							</view>
						</picker>
					</view>
				</view>
				<view class="modal_footer">
					<button class="cancel_btn" @click="closeModal">取消</button>
					<button class="confirm_btn" @click="saveExercise">保存</button>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			showAddModal: false,
			isEdit: false,
			editId: null,
			exerciseTypeIndex: 0,
			exerciseTypes: ['跑步', '游泳', '骑行', '健身', '瑜伽', '篮球', '足球', '羽毛球', '乒乓球', '其他'],
			formData: {
				name: '',
				duration: '',
				calories: '',
				time: '12:00',
				icon: ''
			},
			exerciseList: [
				{
					id: 1,
					name: '跑步',
					duration: 30,
					calories: 300,
					time: '08:30',
					icon: '🏃'
				},
				{
					id: 2,
					name: '游泳',
					duration: 45,
					calories: 450,
					time: '19:00',
					icon: '🏊'
				}
			],
			todayStats: {
				duration: 75,
				calories: 750,
				count: 2
			},
			userInfo: {
				dailyCalorieGoal: 600,
				dailyExerciseGoal: 30
			}
		};
	},
	onLoad() {
		this.loadUserInfo(); // 先加载用户信息
		this.loadExerciseRecords();
		this.updateTodayStats();
	},
	methods: {
		onExerciseTypeChange(e) {
			this.exerciseTypeIndex = e.detail.value;
			this.formData.name = this.exerciseTypes[this.exerciseTypeIndex];
			this.formData.icon = this.getExerciseIcon(this.formData.name);
		},
		onTimeChange(e) {
			this.formData.time = e.detail.value;
		},
		getExerciseIcon(name) {
			const iconMap = {
				'跑步': '🏃',
				'游泳': '🏊',
				'骑行': '🚴',
				'健身': '🏋️',
				'瑜伽': '🧘',
				'篮球': '🏀',
				'足球': '⚽',
				'羽毛球': '🏸',
				'乒乓球': '🏓',
				'其他': '💪'
			};
			return iconMap[name] || '💪';
		},
		editExercise(exercise) {
			this.isEdit = true;
			this.editId = exercise.id;
			this.formData = { ...exercise };
			this.exerciseTypeIndex = this.exerciseTypes.indexOf(exercise.name);
			this.showAddModal = true;
		},
		deleteExercise(id) {
			uni.showModal({
				title: '确认删除',
				content: '确定要删除这条运动记录吗？',
				success: (res) => {
					if (res.confirm) {
						this.exerciseList = this.exerciseList.filter(item => item.id !== id);
						this.updateTodayStats();
					}
				}
			});
		},
		saveExercise() {
			// 检查必填字段
			if (!this.formData.duration || !this.formData.calories) {
				uni.showToast({
					title: '请填写完整信息',
					icon: 'none'
				});
				return;
			}
			
			// 如果没有选择运动类型，使用默认值
			if (!this.formData.name) {
				this.formData.name = this.exerciseTypes[this.exerciseTypeIndex];
				this.formData.icon = this.getExerciseIcon(this.formData.name);
			}
			
			// 准备API数据
			const exerciseData = {
				exerciseType: this.formData.name,
				exerciseName: this.formData.name,
				duration: parseInt(this.formData.duration),
				calories: parseInt(this.formData.calories),
				exerciseTime: new Date().toISOString().replace('Z', 'Z'),
				note: ''
			};
			
			// 添加调试日志
			console.log('发送的运动记录数据:', exerciseData);
			console.log('formData:', this.formData);
			
			const url = this.isEdit ? 
				`http://localhost:8088/api/exercise-records/${this.editId}` : 
				'http://localhost:8088/api/exercise-records';
			const method = this.isEdit ? 'PUT' : 'POST';
			
			// 获取token
			const token = uni.getStorageSync('token')
			
			uni.request({
				url: url,
				method: method,
				data: exerciseData,
				header: {
					'Authorization': token ? 'Bearer ' + token : '',
					'Content-Type': 'application/json'
				},
				success: (res) => {
					if (res.data.code === 0) {
						// 更新本地显示
						if (this.isEdit) {
							const index = this.exerciseList.findIndex(item => item.id === this.editId);
							if (index !== -1) {
								this.exerciseList[index] = { ...this.formData, id: this.editId };
							}
						} else {
							const newExercise = {
								...this.formData,
								id: Date.now()
							};
							this.exerciseList.unshift(newExercise);
						}
						
						this.updateTodayStats();
						this.closeModal();
						uni.showToast({ title: '保存成功', icon: 'success' });
						
						// 通知首页刷新运动数据
						uni.$emit('refreshExerciseData');
					} else {
						uni.showToast({ title: '保存失败', icon: 'none' });
					}
				},
				fail: () => {
					// 如果API调用失败，仍然更新本地显示
					if (this.isEdit) {
						const index = this.exerciseList.findIndex(item => item.id === this.editId);
						if (index !== -1) {
							this.exerciseList[index] = { ...this.formData, id: this.editId };
						}
					} else {
						const newExercise = {
							...this.formData,
							id: Date.now()
						};
						this.exerciseList.unshift(newExercise);
					}
					
					this.updateTodayStats();
					this.closeModal();
					uni.showToast({ title: '保存成功(离线)', icon: 'success' });
					
					// 通知首页刷新运动数据
					uni.$emit('refreshExerciseData');
				}
			});
		},
		updateTodayStats() {
			const today = new Date().toDateString();
			const todayExercises = this.exerciseList.filter(exercise => {
				// 实际项目中，这里需要根据 exercise.time 来判断是否为今天
				// 这里为了示例，直接使用所有数据
				return true;
			});
			
			this.todayStats = {
				duration: todayExercises.reduce((sum, exercise) => sum + parseInt(exercise.duration), 0),
				calories: todayExercises.reduce((sum, exercise) => sum + parseInt(exercise.calories), 0),
				count: todayExercises.length
			};
		},
		closeModal() {
			this.showAddModal = false;
			this.isEdit = false;
			this.editId = null;
			this.formData = {
				name: '',
				duration: '',
				calories: '',
				time: '12:00',
				icon: ''
			};
			this.exerciseTypeIndex = 0;
		},
		// 加载用户信息
		loadUserInfo() {
			const token = uni.getStorageSync('token');
			const storedUserInfo = uni.getStorageSync('userInfo');
			
			if (storedUserInfo) {
				this.userInfo = { ...this.userInfo, ...storedUserInfo };
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
		
		// 加载运动记录
		loadExerciseRecords() {
			const token = uni.getStorageSync('token');
			if (!token) {
				uni.showToast({
					title: '请先登录',
					icon: 'none'
				});
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
						// 将后端数据转换为前端显示格式
						const records = res.data.data.records || [];
						this.exerciseList = records.map(record => ({
							id: record.id,
							name: record.exerciseName || record.exerciseType,
							duration: record.duration,
							calories: record.calories,
							time: this.formatTime(record.exerciseTime),
							icon: this.getExerciseIcon(record.exerciseType || record.exerciseName)
						}));
						this.updateTodayStats();
					}
				},
				fail: () => {
					console.log('加载运动记录失败，使用本地数据');
				}
			});
		},
		
		// 格式化时间为HH:mm格式
		formatTime(dateTimeString) {
			if (!dateTimeString) return '00:00';
			try {
				const date = new Date(dateTimeString);
				const hours = date.getHours().toString().padStart(2, '0');
				const minutes = date.getMinutes().toString().padStart(2, '0');
				return `${hours}:${minutes}`;
			} catch (e) {
				console.error('时间格式化错误:', e);
				return '00:00';
			}
		}
	}
	
};
</script>

<style scoped>
/* 定义颜色变量 */
:root {
  --main-color: #4A89FF;
  --text-color-primary: #333333;
  --text-color-secondary: #666666;
  --border-color: #ebebeb;
  --box-shadow: 0px 2px 8px rgba(99, 99, 99, 0.1);
  --card-background: #ffffff;
}

.exercisePage {
	width: 100%;
	min-height: 100vh;
	background-color: #f2f3f5;
	padding-bottom: 3vh;
}

.header_box {
	width: 95%;
	margin: 0 auto;
	padding: 1em 0;
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.page_title {
	font-size: 24px;
	font-weight: bold;
	color: var(--text-color-primary);
}

.add_exercise_btn {
	background-color: var(--main-color);
	color: white;
	padding: 0.5em 1em;
	border-radius: 20px;
	font-size: 14px;
	min-width: 80px;
	text-align: center;
}

.card_box {
	width: 95%;
	margin: 0 auto 15px;
	background-color: var(--card-background);
	border-radius: 12px;
	box-shadow: var(--box-shadow);
	padding: 20px;
	box-sizing: border-box;
}

.summary_title {
	font-size: 18px;
	font-weight: bold;
	margin-bottom: 1em;
	color: var(--text-color-primary);
}

.summary_stats {
	display: flex;
	justify-content: space-around;
	padding: 15px 0 0;
}

.stat_item {
	text-align: center;
}

.stat_value {
	font-size: 24px;
	font-weight: bold;
	color: var(--main-color);
}

.stat_label {
	font-size: 12px;
	color: var(--text-color-secondary);
	margin-top: 0.5em;
}

.list_title {
	font-size: 18px;
	font-weight: bold;
	padding: 0 0 15px;
	color: var(--text-color-primary);
	border-bottom: 1px solid var(--border-color);
}

.exercise_list {
	padding-top: 15px;
}

.exercise_item {
	display: flex;
	align-items: center;
	padding: 15px 0;
	border-bottom: 1px solid var(--border-color);
}

.exercise_item:last-child {
	border-bottom: none;
}

.exercise_icon {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    overflow: hidden;
    margin-right: 15px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f7f7f7;
}

.icon_emoji {
    font-size: 22px;
}

.exercise_info {
	flex: 1;
}

.exercise_name {
	font-size: 16px;
	font-weight: bold;
	color: var(--text-color-primary);
	margin-bottom: 3px;
}

.exercise_details {
	font-size: 14px;
	color: var(--text-color-secondary);
}

.detail_value {
	color: var(--main-color);
	font-weight: bold;
}

.exercise_actions {
	display: flex;
	gap: 1em;
}

.edit_btn, .delete_btn {
	font-size: 14px;
	padding: 0.3em 0.8em;
	border-radius: 15px;
	line-height: 1.5;
}

.edit_btn {
	background-color: #FFCC00;
	color: white;
}

.delete_btn {
	background-color: #F5296E;
	color: white;
}

/* 模态框样式 */
.modal {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background-color: rgba(0, 0, 0, 0.5);
	display: flex;
	justify-content: center;
	align-items: center;
	z-index: 1000;
}

.modal_content {
	width: 90%;
	max-width: 400px;
	background-color: white;
	border-radius: 12px;
	overflow: hidden;
}

.modal_header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 15px 20px;
	border-bottom: 1px solid var(--border-color);
}

.modal_title {
	font-size: 18px;
	font-weight: bold;
	color: var(--text-color-primary);
}

.close_btn {
	font-size: 24px;
	color: var(--text-color-secondary);
}

.modal_body {
	padding: 20px;
}

.form_item {
	margin-bottom: 15px;
}

.form_label {
	display: block;
	font-size: 14px;
	color: var(--text-color-primary);
	margin-bottom: 8px;
}

.picker_input, input {
	width: 100%;
	height: 44px;
	line-height: 44px;
	padding: 0 12px;
	border: 1px solid var(--border-color);
	border-radius: 8px;
	font-size: 14px;
	box-sizing: border-box;
}

.modal_footer {
	display: flex;
	gap: 10px;
	padding: 15px 20px;
	border-top: 1px solid var(--border-color);
}

.cancel_btn, .confirm_btn {
	flex: 1;
	height: 44px;
	line-height: 44px;
	border: none;
	border-radius: 8px;
	font-size: 16px;
	text-align: center;
}

.cancel_btn {
	background-color: #f5f5f5;
	color: #666;
}

.confirm_btn {
	background-color: var(--main-color);
	color: white;
}
</style>