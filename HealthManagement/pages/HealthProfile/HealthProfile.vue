<template>
	<view class="healthProfile">
		<view class="header">
			<text class="title">健康档案</text>
		</view>
		
		<view class="profile-card">
			<view class="card-title">基本信息</view>
			<view class="form-row">
				<view class="label">身高 (cm)</view>
				<input 
					class="input" 
					type="number" 
					v-model.number="profile.height" 
					placeholder="请输入身高"
					@blur="calculateBMI"
				/>
			</view>
			<view class="form-row">
				<view class="label">体重 (kg)</view>
				<input 
					class="input" 
					type="number" 
					v-model.number="profile.weight" 
					placeholder="请输入体重"
					@blur="calculateBMI"
				/>
			</view>
			<view class="form-row" v-if="profile.bmi">
				<view class="label">BMI指数</view>
				<view class="bmi-info">
					<text class="bmi-value">{{ profile.bmi }}</text>
					<text class="bmi-category" :class="getBmiCategoryClass(profile.bmi)">
						{{ getBmiCategory(profile.bmi) }}
					</text>
				</view>
			</view>
		</view>
		
		<view class="profile-card">
			<view class="card-title">健康指标</view>
			<view class="form-row">
				<view class="label">收缩压</view>
				<input 
					class="input" 
					type="number" 
					v-model.number="profile.bloodPressureSystolic" 
					placeholder="请输入收缩压"
				/>
			</view>
			<view class="form-row">
				<view class="label">舒张压</view>
				<input 
					class="input" 
					type="number" 
					v-model.number="profile.bloodPressureDiastolic" 
					placeholder="请输入舒张压"
				/>
			</view>
			<view class="form-row">
				<view class="label">血糖值</view>
				<input 
					class="input" 
					type="number" 
					v-model.number="profile.bloodSugar" 
					placeholder="请输入血糖值"
				/>
			</view>
			<view class="form-row">
				<view class="label">胆固醇值</view>
				<input 
					class="input" 
					type="number" 
					v-model.number="profile.cholesterol" 
					placeholder="请输入胆固醇值"
				/>
			</view>
		</view>
		
		<view class="profile-card">
			<view class="card-title">健康历史</view>
			<view class="form-row">
				<view class="label">病史</view>
				<textarea 
					class="textarea" 
					v-model="profile.medicalHistory" 
					placeholder="请输入病史信息"
				/>
			</view>
			<view class="form-row">
				<view class="label">过敏史</view>
				<textarea 
					class="textarea" 
					v-model="profile.allergies" 
					placeholder="请输入过敏史信息"
				/>
			</view>
			<view class="form-row">
				<view class="label">用药情况</view>
				<textarea 
					class="textarea" 
					v-model="profile.medications" 
					placeholder="请输入用药情况"
				/>
			</view>
		</view>
		
		<view class="profile-card">
			<view class="card-title">紧急联系人</view>
			<view class="form-row">
				<view class="label">联系人姓名</view>
				<input 
					class="input" 
					v-model="profile.emergencyContact" 
					placeholder="请输入紧急联系人姓名"
				/>
			</view>
			<view class="form-row">
				<view class="label">联系电话</view>
				<input 
					class="input" 
					v-model="profile.emergencyPhone" 
					placeholder="请输入紧急联系电话"
				/>
			</view>
		</view>
		
		<view class="actions">
			<button class="btn-save" @click="saveProfile">保存档案</button>
		</view>
	</view>
</template>

<script>
import apiConfig from '@/api/config.js';

export default {
	data() {
		return {
			profile: {
				height: null,
				weight: null,
				bmi: null,
				bloodPressureSystolic: null,
				bloodPressureDiastolic: null,
				bloodSugar: null,
				cholesterol: null,
				medicalHistory: '',
				allergies: '',
				medications: '',
				emergencyContact: '',
				emergencyPhone: ''
			}
		};
	},
	onLoad() {
		this.loadProfile();
	},
	methods: {
		// 加载健康档案
		loadProfile() {
			const token = uni.getStorageSync('token');
			if (!token) {
				uni.showToast({
					title: '请先登录',
					icon: 'none'
				});
				return;
			}
			
			uni.request({
				url: apiConfig.API_CONFIG.BASE_URL + apiConfig.API_CONFIG.HEALTH_PROFILES.ME,
				method: 'GET',
				header: {
					'Authorization': 'Bearer ' + token
				},
				success: (res) => {
					if (res.data.code === 0 && res.data.data) {
						this.profile = { ...this.profile, ...res.data.data };
					}
				},
				fail: () => {
					console.log('加载健康档案失败');
				}
			});
		},
		
		// 计算BMI
		calculateBMI() {
			if (this.profile.height && this.profile.weight && this.profile.height > 0) {
				const heightInMeters = this.profile.height / 100;
				this.profile.bmi = Math.round((this.profile.weight / (heightInMeters * heightInMeters)) * 100) / 100;
			}
		},
		
		// 获取BMI分类
		getBmiCategory(bmi) {
			if (bmi < 18.5) return '偏瘦';
			if (bmi < 24) return '正常';
			if (bmi < 28) return '超重';
			return '肥胖';
		},
		
		// 获取BMI分类样式
		getBmiCategoryClass(bmi) {
			if (bmi < 18.5) return 'bmi-underweight';
			if (bmi < 24) return 'bmi-normal';
			if (bmi < 28) return 'bmi-overweight';
			return 'bmi-obese';
		},
		
		// 保存健康档案
		saveProfile() {
			const token = uni.getStorageSync('token');
			if (!token) {
				uni.showToast({
					title: '请先登录',
					icon: 'none'
				});
				return;
			}
			
			uni.request({
				url: apiConfig.API_CONFIG.BASE_URL + apiConfig.API_CONFIG.HEALTH_PROFILES.UPDATE,
				method: 'POST',
				data: this.profile,
				header: {
					'Authorization': 'Bearer ' + token
				},
				success: (res) => {
					if (res.data.code === 0) {
						uni.showToast({
							title: '保存成功',
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
					uni.showToast({
						title: '网络错误',
						icon: 'none'
					});
				}
			});
		}
	}
};
</script>

<style scoped>
.healthProfile {
	padding: 20px;
	background: #f5f5f5;
	min-height: 100vh;
}

.header {
	text-align: center;
	margin-bottom: 20px;
}

.title {
	font-size: 24px;
	font-weight: bold;
	color: #333;
}

.profile-card {
	background: #fff;
	border-radius: 12px;
	padding: 20px;
	margin-bottom: 20px;
	box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.card-title {
	font-size: 18px;
	font-weight: bold;
	color: #333;
	margin-bottom: 16px;
	border-bottom: 2px solid #f0f0f0;
	padding-bottom: 8px;
}

.form-row {
	display: flex;
	align-items: center;
	margin-bottom: 16px;
}

.label {
	width: 120px;
	font-size: 16px;
	color: #666;
	flex-shrink: 0;
}

.input, .textarea {
	flex: 1;
	height: 44px;
	line-height: 44px;
	background: #f8f8f8;
	border-radius: 8px;
	padding: 0 12px;
	font-size: 16px;
	border: 1px solid #e0e0e0;
	box-sizing: border-box;
}

.textarea {
	height: 80px;
	line-height: 1.5;
	padding: 12px;
}

.bmi-info {
	flex: 1;
	display: flex;
	align-items: center;
	gap: 12px;
}

.bmi-value {
	font-size: 20px;
	font-weight: bold;
	color: #333;
}

.bmi-category {
	padding: 4px 12px;
	border-radius: 16px;
	font-size: 14px;
	font-weight: bold;
}

.bmi-underweight {
	background: #e6f7ff;
	color: #1890ff;
}

.bmi-normal {
	background: #f6ffed;
	color: #52c41a;
}

.bmi-overweight {
	background: #fff7e6;
	color: #fa8c16;
}

.bmi-obese {
	background: #fff2f0;
	color: #ff4d4f;
}

.actions {
	padding: 20px 0;
}

.btn-save {
	width: 100%;
	height: 48px;
	background: #F5296E;
	color: #fff;
	border: none;
	border-radius: 8px;
	font-size: 16px;
	font-weight: bold;
}
</style>


