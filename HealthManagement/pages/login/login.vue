<template>
	<view class="login-container">
		<view class="login-header">
			<image class="logo" src="/static/logo.png" mode="aspectFit"></image>
			<text class="app-name">健康管理</text>
			<text class="app-desc">记录健康，管理生活</text>
		</view>
		
		<view class="login-content">
			<button 
				class="login-btn" 
				@click="handleWechatLogin"
				:loading="isLoading"
				:disabled="isLoading"
			>
				<text class="btn-icon">🔐</text>
				<text class="btn-text">{{ isLoading ? '登录中...' : '微信授权登录' }}</text>
			</button>
			
			<view class="login-tips">
				<text class="tips-text">登录即表示同意</text>
				<text class="link-text" @click="showPrivacy">《隐私政策》</text>
				<text class="tips-text">和</text>
				<text class="link-text" @click="showTerms">《用户协议》</text>
			</view>
		</view>
		
		<view class="login-footer">
			<text class="version">v1.0.0</text>
		</view>
	</view>
</template>

<script>
import apiConfig from '@/api/config.js'

export default {
	data() {
		return {
			isLoading: false
		}
	},
	onLoad() {
		// 检查是否已登录
		this.checkLoginStatus()
	},
	methods: {
		// 检查登录状态
		checkLoginStatus() {
			const token = uni.getStorageSync('token')
			const userInfo = uni.getStorageSync('userInfo')
			
			if (token && userInfo) {
				// 验证token有效性
				this.validateToken(token)
			}
		},
		
		// 验证token有效性
		validateToken(token) {
			uni.request({
				url: apiConfig.API_CONFIG.BASE_URL + apiConfig.API_CONFIG.AUTH.ME,
				method: 'GET',
				header: {
					'Authorization': 'Bearer ' + token
				},
				success: (res) => {
					if (res.data.code === 0) {
						// token有效，直接跳转到首页
						uni.switchTab({
							url: '/pages/index/index'
						})
					} else {
						// token无效，清除本地存储
						this.clearLoginData()
					}
				},
				fail: () => {
					// 网络错误，清除本地存储
					this.clearLoginData()
				}
			})
		},
		
		// 微信登录
		handleWechatLogin() {
			if (this.isLoading) return
			
			this.isLoading = true
			
			// 获取微信登录code
			uni.login({
				provider: 'weixin',
				success: (loginRes) => {
					if (loginRes.code) {
						// 调用后端登录接口
						this.loginWithCode(loginRes.code)
					} else {
						this.isLoading = false
						uni.showToast({
							title: '获取登录凭证失败',
							icon: 'none'
						})
					}
				},
				fail: (err) => {
					this.isLoading = false
					console.error('微信登录失败:', err)
					uni.showToast({
						title: '微信登录失败',
						icon: 'none'
					})
				}
			})
		},
		
		// 使用code登录
		loginWithCode(code) {
			uni.request({
				url: apiConfig.API_CONFIG.BASE_URL + apiConfig.API_CONFIG.AUTH.LOGIN,
				method: 'POST',
				data: {
					code: code
				},
				success: (res) => {
					this.isLoading = false
					
					if (res.data.code === 0) {
						const { token, user } = res.data.data
						
						// 保存登录信息
						uni.setStorageSync('token', token)
						uni.setStorageSync('userInfo', user)
						
						uni.showToast({
							title: '登录成功',
							icon: 'success'
						})
						
						// 跳转到首页
						setTimeout(() => {
							uni.switchTab({
								url: '/pages/index/index'
							})
						}, 1500)
					} else {
						uni.showToast({
							title: res.data.message || '登录失败',
							icon: 'none'
						})
					}
				},
				fail: (err) => {
					this.isLoading = false
					console.error('登录请求失败:', err)
					uni.showToast({
						title: '网络错误，请重试',
						icon: 'none'
					})
				}
			})
		},
		
		// 清除登录数据
		clearLoginData() {
			uni.removeStorageSync('token')
			uni.removeStorageSync('userInfo')
		},
		
		// 显示隐私政策
		showPrivacy() {
			uni.showModal({
				title: '隐私政策',
				content: '我们重视您的隐私保护...',
				showCancel: false
			})
		},
		
		// 显示用户协议
		showTerms() {
			uni.showModal({
				title: '用户协议',
				content: '欢迎使用健康管理应用...',
				showCancel: false
			})
		}
	}
}
</script>

<style scoped>
.login-container {
	width: 100%;
	height: 100vh;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 40rpx;
	box-sizing: border-box;
}

.login-header {
	text-align: center;
	margin-bottom: 120rpx;
}

.logo {
	width: 120rpx;
	height: 120rpx;
	margin-bottom: 30rpx;
}

.app-name {
	display: block;
	font-size: 48rpx;
	font-weight: bold;
	color: #ffffff;
	margin-bottom: 20rpx;
}

.app-desc {
	display: block;
	font-size: 28rpx;
	color: rgba(255, 255, 255, 0.8);
}

.login-content {
	width: 100%;
	max-width: 600rpx;
}

.login-btn {
	width: 100%;
	height: 88rpx;
	background: #ffffff;
	border-radius: 44rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-bottom: 40rpx;
	box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.1);
}

.btn-icon {
	font-size: 32rpx;
	margin-right: 16rpx;
}

.btn-text {
	font-size: 32rpx;
	font-weight: 500;
	color: #333333;
}

.login-tips {
	text-align: center;
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.7);
	line-height: 1.5;
}

.link-text {
	color: #ffffff;
	text-decoration: underline;
}

.login-footer {
	position: absolute;
	bottom: 60rpx;
	text-align: center;
}

.version {
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.5);
}
</style>

