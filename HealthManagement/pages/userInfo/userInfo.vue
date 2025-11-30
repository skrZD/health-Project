<template>
	<view class="userInfoPage">
		<view class="profile_box card_box">
			<view class="profile_header">
				<view class="avatar_container">
					<image class="user_avatar" :src="userInfo.avatar" mode="aspectFill"/>
					<view class="avatar_edit" @click="changeAvatar">
						<text>编辑</text>
					</view>
				</view>
				<view class="user_basic">
					<view class="user_name">{{userInfo.name}}</view>
					<view class="user_id">ID: {{userInfo.id}}</view>
					<view class="user_level">
						<text class="level_icon">🏆</text>
						<text>{{userInfo.level}}</text>
					</view>
				</view>
			</view>
			<view class="profile_stats">
				<view class="stat_item">
					<view class="stat_number">{{userInfo.totalDays}}</view>
					<view class="stat_label">坚持天数</view>
				</view>
				<view class="stat_item">
					<view class="stat_number">{{userInfo.totalCalories}}</view>
					<view class="stat_label">总消耗(千卡)</view>
				</view>
				<view class="stat_item">
					<view class="stat_number">{{userInfo.totalMinutes}}</view>
					<view class="stat_label">总运动(分钟)</view>
				</view>
			</view>
		</view>

		<view class="info_settings_box card_box">
			<view class="section_title">
				个人信息
			</view>
			<view class="settings_list">
				<view class="setting_item" @click="editInfo('name')">
					<view class="setting_icon">
						<text>👤</text>
					</view>
					<view class="setting_content">
						<view class="setting_label">姓名</view>
						<view class="setting_value">{{userInfo.name}}</view>
					</view>
					<!-- <view class="setting_arrow">></view> -->
				</view>
				<view class="setting_item" @click="editInfo('age')">
					<view class="setting_icon">
						<text>🎂</text>
					</view>
					<view class="setting_content">
						<view class="setting_label">年龄</view>
						<view class="setting_value">{{userInfo.age}}岁</view>
					</view>
					<!-- <view class="setting_arrow">></view> -->
				</view>
				<view class="setting_item" @click="editInfo('height')">
					<view class="setting_icon">
						<text>📏</text>
					</view>
					<view class="setting_content">
						<view class="setting_label">身高</view>
						<view class="setting_value">{{userInfo.height}}cm</view>
					</view>
					<!-- <view class="setting_arrow">></view> -->
				</view>
				<view class="setting_item" @click="editInfo('weight')">
					<view class="setting_icon">
						<text>⚖️</text>
					</view>
					<view class="setting_content">
						<view class="setting_label">体重</view>
						<view class="setting_value">{{userInfo.weight}}kg</view>
					</view>
					<!-- <view class="setting_arrow">></view> -->
				</view>
				<view class="setting_item" @click="editInfo('gender')">
					<view class="setting_icon">
						<text>⚧</text>
					</view>
					<view class="setting_content">
						<view class="setting_label">性别</view>
						<view class="setting_value">{{userInfo.gender}}</view>
					</view>
					<!-- <view class="setting_arrow">></view> -->
				</view>
				<view class="setting_item" @click="goToHealthProfile">
					<view class="setting_icon">
						<text>🏥</text>
					</view>
					<view class="setting_content">
						<view class="setting_label">健康档案</view>
						<view class="setting_value">管理健康信息</view>
					</view>
					<!-- <view class="setting_arrow">></view> -->
				</view>
			</view>
		</view>

		<view class="goals_box card_box">
			<view class="section_title">
				目标设置
			</view>
			<view class="goals_list">
				<view class="goal_item">
					<view class="goal_icon">
						<text class="dot" style="color: var(--danger-color);">●</text>
					</view>
					<view class="goal_content">
						<view class="goal_label">每日热量目标</view>
						<view class="goal_value">{{userInfo.dailyCalorieGoal}} 千卡</view>
					</view>
					<view class="goal_edit_btn" @click="editGoal('calorie')">
						<text>编辑</text>
					</view>
				</view>
				<view class="goal_item">
					<view class="goal_icon">
						<text class="dot" style="color: var(--warning-color);">●</text>
					</view>
					<view class="goal_content">
						<view class="goal_label">每日运动时长</view>
						<view class="goal_value">{{userInfo.dailyExerciseGoal}} 分钟</view>
					</view>
					<view class="goal_edit_btn" @click="editGoal('exercise')">
						<text>编辑</text>
					</view>
				</view>
				<view class="goal_item">
					<view class="goal_icon">
						<text class="dot" style="color: var(--main-color);">●</text>
					</view>
					<view class="goal_content">
						<view class="goal_label">每日步数目标</view>
						<view class="goal_value">{{userInfo.dailyStepGoal}} 步</view>
					</view>
					<view class="goal_edit_btn" @click="editGoal('steps')">
						<text>编辑</text>
					</view>
				</view>
			</view>
		</view>

		<view class="app_settings_box card_box">
			<view class="section_title">
				应用设置
			</view>
			<view class="settings_list">
				<view class="setting_item" @click="toggleSetting('notifications')">
					<view class="setting_icon">
						<text>🔔</text>
					</view>
					<view class="setting_content">
						<view class="setting_label">消息通知</view>
						<view class="setting_desc">接收运动提醒和成就通知</view>
					</view>
					<view class="setting_switch">
						<switch :checked="appSettings.notifications" @change="onNotificationChange"/>
					</view>
				</view>
				<view class="setting_item" @click="toggleSetting('privacy')">
					<view class="setting_icon">
						<text>🔒</text>
					</view>
					<view class="setting_content">
						<view class="setting_label">隐私保护</view>
						<view class="setting_desc">保护个人数据安全</view>
					</view>
					<view class="setting_switch">
						<switch :checked="appSettings.privacy" @change="onPrivacyChange"/>
					</view>
				</view>
				<view class="setting_item" @click="goToPage('about')">
					<view class="setting_icon">
						<text>ℹ️</text>
					</view>
					<view class="setting_content">
						<view class="setting_label">关于我们</view>
						<view class="setting_desc">版本信息和使用帮助</view>
					</view>
					<view class="setting_arrow">></view>
				</view>
				<view class="setting_item" @click="goToPage('feedback')">
					<view class="setting_icon">
						<text>💬</text>
					</view>
					<view class="setting_content">
						<view class="setting_label">意见反馈</view>
						<view class="setting_desc">帮助我们改进产品</view>
					</view>
					<view class="setting_arrow">></view>
				</view>
			</view>
		</view>

		<view class="logout_box">
			<view class="logout_btn" @click="logout">
				<text>退出登录</text>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			userInfo: {
				avatar: '/static/default_avatar.png',
				name: '用户',
				id: '',
				level: '健身新手',
				totalDays: 0,
				totalCalories: 0,
				totalMinutes: 0,
				age: 25,
				height: 170,
				weight: 65,
				gender: '男',
				dailyCalorieGoal: 600,
				dailyExerciseGoal: 30,
				dailyStepGoal: 10000
			},
			appSettings: {
				notifications: true,
				privacy: true
			},
			isLoading: false
		};
	},
	onShow() {
		this.loadUserInfo();
		this.loadUserStats();
	},
	methods: {
		// 加载用户信息
		loadUserInfo() {
			const token = uni.getStorageSync('token');
			const storedUserInfo = uni.getStorageSync('userInfo');
			
			if (storedUserInfo) {
				this.userInfo = { ...this.userInfo, ...storedUserInfo };
			}
			
			if (!token) {
				uni.showToast({
					title: '请先登录',
					icon: 'none'
				});
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
							...userData,
							avatar: userData.avatar || '/static/default_avatar.png'
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
		
		// 加载用户统计数据
		loadUserStats() {
			const token = uni.getStorageSync('token');
			if (!token) return;
			
			// 获取运动统计
			uni.request({
				url: 'http://localhost:8088/api/exercise-records/stats',
				method: 'GET',
				header: {
					'Authorization': 'Bearer ' + token
				},
				success: (res) => {
					if (res.data.code === 0) {
						const stats = res.data.data;
						this.userInfo.totalCalories = stats.totalCalories || 0;
						this.userInfo.totalMinutes = stats.totalMinutes || 0;
						this.userInfo.totalDays = stats.totalDays || 0;
					}
				},
				fail: () => {
					console.log('加载运动统计失败');
				}
			});
		},
		
		// 上传头像
		uploadAvatar(filePath) {
			const token = uni.getStorageSync('token');
			if (!token) {
				uni.showToast({
					title: '请先登录',
					icon: 'none'
				});
				return;
			}
			
			uni.uploadFile({
				url: 'http://localhost:8088/api/users/avatar',
				filePath: filePath,
				name: 'avatar',
				header: {
					'Authorization': 'Bearer ' + token
				},
				success: (res) => {
					const data = JSON.parse(res.data);
					if (data.code === 0) {
						this.userInfo.avatar = data.data.avatarUrl;
						uni.setStorageSync('userInfo', this.userInfo);
						uni.showToast({
							title: '头像上传成功',
							icon: 'success'
						});
					} else {
						uni.showToast({
							title: '头像上传失败',
							icon: 'none'
						});
					}
				},
				fail: () => {
					uni.showToast({
						title: '头像上传失败',
						icon: 'none'
					});
				}
			});
		},
		
		changeAvatar() {
			uni.chooseImage({
				count: 1,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success: (res) => {
					const tempFilePath = res.tempFilePaths[0];
					// 先显示选中的图片
					this.userInfo.avatar = tempFilePath;
					// 然后上传到服务器
					this.uploadAvatar(tempFilePath);
				},
				fail: () => {
					uni.showToast({
						title: '选择图片失败',
						icon: 'none'
					});
				}
			});
		},
		editInfo(type) {
			const currentValue = this.userInfo[type];
			const placeholder = type === 'age' || type === 'height' || type === 'weight' ? 
				`当前值: ${currentValue}` : `请输入${this.getFieldName(type)}`;
			
			uni.showModal({
				title: '编辑' + this.getFieldName(type),
				editable: true,
				placeholderText: placeholder,
				success: (res) => {
					if (res.confirm && res.content) {
						let newValue = res.content;
						
						// 数据类型转换
						if (type === 'age' || type === 'height' || type === 'weight') {
							newValue = parseFloat(newValue);
							if (isNaN(newValue)) {
								uni.showToast({
									title: '请输入有效数字',
									icon: 'none'
								});
								return;
							}
						}
						
						// 更新本地数据
						this.userInfo[type] = newValue;
						
						// 保存到后端
						this.saveUserInfo();
						
						uni.showToast({
							title: '更新成功',
							icon: 'success'
						});
					}
				}
			});
		},
		editGoal(type) {
			const goalNames = {
				calorie: '每日热量目标',
				exercise: '每日运动时长',
				steps: '每日步数目标'
			};
			
			const currentValue = type === 'calorie' ? this.userInfo.dailyCalorieGoal :
								type === 'exercise' ? this.userInfo.dailyExerciseGoal :
								this.userInfo.dailyStepGoal;
			
			uni.showModal({
				title: '编辑' + goalNames[type],
				editable: true,
				placeholderText: `当前值: ${currentValue}`,
				success: (res) => {
					if (res.confirm && res.content) {
						const value = parseInt(res.content);
						if (!isNaN(value) && value > 0) {
							// 更新本地数据
							if (type === 'calorie') {
								this.userInfo.dailyCalorieGoal = value;
							} else if (type === 'exercise') {
								this.userInfo.dailyExerciseGoal = value;
							} else if (type === 'steps') {
								this.userInfo.dailyStepGoal = value;
							}
							
							// 保存到后端
							this.saveUserGoals();
							
							uni.showToast({
								title: '目标更新成功',
								icon: 'success'
							});
						} else {
							uni.showToast({
								title: '请输入有效数字',
								icon: 'none'
							});
						}
					}
				}
			});
		},
		// 保存用户信息到后端
		saveUserInfo() {
			const token = uni.getStorageSync('token');
			if (!token) return;
			
			const userData = {
				name: this.userInfo.name,
				age: this.userInfo.age,
				height: this.userInfo.height,
				weight: this.userInfo.weight,
				gender: this.userInfo.gender
			};
			
			uni.request({
				url: 'http://localhost:8088/api/users/me',
				method: 'PUT',
				data: userData,
				header: {
					'Authorization': 'Bearer ' + token,
					'Content-Type': 'application/json'
				},
				success: (res) => {
					if (res.data.code === 0) {
						// 保存到本地存储
						uni.setStorageSync('userInfo', this.userInfo);
						console.log('用户信息保存成功');
					} else {
						console.log('用户信息保存失败:', res.data.message);
					}
				},
				fail: () => {
					console.log('用户信息保存失败');
				}
			});
		},
		
		// 保存用户目标到后端
		saveUserGoals() {
			const token = uni.getStorageSync('token');
			if (!token) return;
			
			const goals = {
				dailyCalorieGoal: this.userInfo.dailyCalorieGoal,
				dailyExerciseGoal: this.userInfo.dailyExerciseGoal,
				dailyStepGoal: this.userInfo.dailyStepGoal
			};
			
			uni.request({
				url: 'http://localhost:8088/api/users/goals',
				method: 'PUT',
				data: goals,
				header: {
					'Authorization': 'Bearer ' + token,
					'Content-Type': 'application/json'
				},
				success: (res) => {
					if (res.data.code === 0) {
						// 保存到本地存储
						uni.setStorageSync('userInfo', this.userInfo);
						console.log('用户目标保存成功');
					} else {
						console.log('用户目标保存失败:', res.data.message);
					}
				},
				fail: () => {
					console.log('用户目标保存失败');
				}
			});
		},
		
		getFieldName(type) {
			const names = {
				name: '姓名',
				age: '年龄',
				height: '身高',
				weight: '体重',
				gender: '性别'
			};
			return names[type] || '';
		},
		toggleSetting(type) {
			this.appSettings[type] = !this.appSettings[type];
		},
		onNotificationChange(e) {
			this.appSettings.notifications = e.detail.value;
			uni.showToast({
				title: this.appSettings.notifications ? '通知已开启' : '通知已关闭',
				icon: 'none'
			});
		},
		onPrivacyChange(e) {
			this.appSettings.privacy = e.detail.value;
			uni.showToast({
				title: this.appSettings.privacy ? '隐私保护已开启' : '隐私保护已关闭',
				icon: 'none'
			});
		},
		goToPage(page) {
			if (page === 'about') {
				uni.navigateTo({
					url: '/pages/about/about'
				});
			} else if (page === 'feedback') {
				uni.navigateTo({
					url: '/pages/feedback/feedback'
				});
			}
		},
		goToHealthProfile() {
			uni.navigateTo({
				url: '/pages/HealthProfile/HealthProfile'
			});
		},
		logout() {
			uni.showModal({
				title: '确认退出',
				content: '确定要退出登录吗？',
				success: (res) => {
					if (res.confirm) {
						// 清除本地存储
						uni.removeStorageSync('token');
						uni.removeStorageSync('userInfo');
						
						// 跳转到登录页面
						uni.reLaunch({
							url: '/pages/login/login'
						});
						
						uni.showToast({
							title: '已退出登录',
							icon: 'success'
						});
					}
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

.userInfoPage {
	width: 100%;
	min-height: 100vh;
	padding-bottom: 3vh;
	background-color: var(--background-color);
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

.profile_header {
	display: flex;
	align-items: center;
	padding-bottom: 20px;
}

.avatar_container {
	position: relative;
	margin-right: 20px;
}

.user_avatar {
	width: 80px;
	height: 80px;
	border-radius: 50%;
	border: 3px solid #f0f0f0;
}

.avatar_edit {
	position: absolute;
	bottom: 0;
	right: 0;
	background-color: var(--danger-color);
	color: white;
	padding: 2px 8px;
	border-radius: 12px;
	font-size: 12px;
	line-height: 1.5;
}

.user_basic {
	flex: 1;
}

.user_name {
	font-size: 20px;
	font-weight: bold;
	color: var(--text-color-primary);
	margin-bottom: 5px;
}

.user_id {
	font-size: 14px;
	color: var(--text-color-secondary);
	margin-bottom: 5px;
}

.user_level {
	display: flex;
	align-items: center;
	font-size: 14px;
	color: var(--danger-color);
}

.level_icon {
	margin-right: 5px;
}

.profile_stats {
	display: flex;
	justify-content: space-around;
	padding-top: 20px;
	border-top: 1px solid var(--border-color);
}

.stat_item {
	text-align: center;
}

.stat_number {
	font-size: 18px;
	font-weight: bold;
	color: var(--text-color-primary);
}

.stat_label {
	font-size: 12px;
	color: var(--text-color-secondary);
	margin-top: 5px;
}

.section_title {
	font-size: 18px;
	font-weight: bold;
	color: var(--text-color-primary);
	padding-bottom: 15px;
	border-bottom: 1px solid var(--border-color);
	margin-bottom: 15px;
}

.setting_item, .goal_item {
	display: flex;
	align-items: center;
	padding: 15px 0;
	border-bottom: 1px solid var(--border-color);
}

.setting_item:last-child, .goal_item:last-child {
	border-bottom: none;
}

.setting_icon, .goal_icon {
	width: 40px;
	height: 40px;
	line-height: 40px;
	text-align: center;
	font-size: 20px;
	margin-right: 15px;
}

.setting_content, .goal_content {
	flex: 1;
}

.setting_label, .goal_label {
	font-weight: bold;
	color: var(--text-color-primary);
	margin-bottom: 5px;
}

.setting_value, .goal_value {
	font-size: 14px;
	color: var(--text-color-secondary);
}

.setting_desc {
	font-size: 12px;
	color: #999;
}

.setting_arrow {
	color: #ccc;
	font-size: 16px;
}

.goal_edit_btn {
	background-color: var(--main-color);
	color: white;
	padding: 5px 12px;
	border-radius: 15px;
	font-size: 12px;
}

.dot {
	font-size: 30px;
	line-height: 1;
}

.setting_switch {
	transform: scale(0.8);
}

.logout_box {
	width: 95%;
	margin: 15px auto;
}

.logout_btn {
	background-color: #ff4757;
	color: white;
	text-align: center;
	padding: 15px;
	border-radius: 12px;
	font-weight: bold;
}
</style>