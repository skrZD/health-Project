<template>
	<view class="weightPage">
		<view class="header_box">
			<view class="page_title">
				体重管理
			</view>
			<view class="add_weight_btn" @click="showAddModal = true">
				<text>+ 记录体重</text>
			</view>
		</view>
		
		<view class="bmi_box card_box">
			<view class="bmi_title">BMI指数</view>
			<view class="bmi_content">
				<view class="bmi_value">{{currentBMI}}<text class="unit">kg/m²</text></view>
				<view class="bmi_status" :class="bmiStatusClass">{{bmiStatus}}</view>
			</view>
			<view class="bmi_info">
				<text>身高: {{userInfo.height}}cm</text>
				<text>体重: {{userInfo.weight}}kg</text>
			</view>
		</view>
		
		<view class="weight_records_box card_box">
			<view class="records_title">体重记录</view>
			<view class="weight_records">
				<view class="weight_record list_item" v-for="record in weightRecords" :key="record.id">
					<view class="record_date">{{record.date}}</view>
					<view class="record_weight">{{record.weight}}kg</view>
					<view class="record_bmi">BMI: {{record.bmi}}</view>
					<view class="record_change" :class="record.changeClass">
						{{record.change}}
					</view>
				</view>
			</view>
		</view>
		
		<view class="modal" v-if="showAddModal" @click="showAddModal = false">
			<view class="modal_content" @click.stop>
				<view class="modal_title">记录体重</view>
				<view class="form_item">
					<text class="form_label">体重(kg)</text>
					<input class="form_input" type="number" v-model="newWeight.weight" placeholder="请输入体重" step="0.1"/>
				</view>
				<view class="form_item">
					<text class="form_label">记录日期</text>
					<picker mode="date" :value="newWeight.date" @change="onDateChange">
						<view class="picker">{{newWeight.date}}</view>
					</picker>
				</view>
				<view class="form_item">
					<text class="form_label">备注</text>
					<input class="form_input" v-model="newWeight.note" placeholder="可选，如：运动后"/>
				</view>
				<view class="modal_actions">
					<button class="cancel_btn" @click="showAddModal = false">取消</button>
					<button class="confirm_btn" @click="addWeightRecord">确定</button>
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
			userInfo: {
				height: 175,
				weight: 70
			},
			newWeight: {
				weight: '',
				date: '',
				note: ''
			},
			weightRecords: [], // Initializing with an empty array to load from API
		};
	},
	computed: {
		currentBMI() {
			return this.calculateBMI(this.userInfo.weight);
		},
		bmiStatus() {
			const bmi = parseFloat(this.currentBMI);
			if (bmi < 18.5) return '偏瘦';
			if (bmi < 24) return '正常';
			if (bmi < 28) return '偏胖';
			return '肥胖';
		},
		bmiStatusClass() {
			const bmi = parseFloat(this.currentBMI);
			if (bmi < 18.5) return 'underweight';
			if (bmi < 24) return 'normal';
			if (bmi < 28) return 'overweight';
			return 'obese';
		}
	},
	onLoad() {
		this.newWeight.date = this.getCurrentDate();
		this.loadUserInfo();
		this.loadWeightRecords();
	},
	onShow() {
		this.loadUserInfo();
	},
	methods: {
		onDateChange(e) {
			this.newWeight.date = e.detail.value;
		},
		addWeightRecord() {
			if (!this.newWeight.weight || !this.newWeight.date) {
				uni.showToast({
					title: '请填写完整信息',
					icon: 'none'
				});
				return;
			}
			
			const weight = parseFloat(this.newWeight.weight);
			const bmi = this.calculateBMI(weight);
			
			const weightData = {
				weight: weight,
				recordedAt: this.newWeight.date + 'T12:00:00',
				note: this.newWeight.note
			};
			
			const token = uni.getStorageSync('token');
			
			uni.request({
				url: 'http://localhost:8088/api/weight-records',
				method: 'POST',
				data: weightData,
				header: {
					'Authorization': token ? 'Bearer ' + token : ''
				},
				success: (res) => {
					if (res.data.code === 0) {
						uni.showToast({
							title: '记录成功',
							icon: 'success'
						});
						this.loadWeightRecords();
						this.showAddModal = false;
						this.newWeight = { weight: '', date: this.getCurrentDate(), note: '' };
					} else {
						uni.showToast({ title: '保存失败', icon: 'none' });
					}
				},
				fail: () => {
					uni.showToast({
						title: '保存失败，请检查网络',
						icon: 'none'
					});
					this.showAddModal = false;
					this.newWeight = { weight: '', date: this.getCurrentDate(), note: '' };
				}
			});
		},
		loadUserInfo() {
			const token = uni.getStorageSync('token');
			const storedUserInfo = uni.getStorageSync('userInfo');
			
			if (storedUserInfo) {
				this.userInfo = { ...this.userInfo, ...storedUserInfo };
			}
			
			if (!token) {
				return;
			}
			
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
						uni.setStorageSync('userInfo', this.userInfo);
						this.updateBMIForAllRecords();
					}
				}
			});
		},
		updateBMIForAllRecords() {
			if (!this.userInfo.height || this.userInfo.height <= 0) {
				return;
			}
			
			this.weightRecords.forEach(record => {
				record.bmi = this.calculateBMI(record.weight);
			});
		},
		calculateBMI(weight) {
			if (!this.userInfo.height || this.userInfo.height <= 0 || !weight) {
				return 0;
			}
			const height = this.userInfo.height / 100;
			return parseFloat((weight / (height * height)).toFixed(1));
		},
		loadWeightRecords() {
			const token = uni.getStorageSync('token');
			if (!token) {
				return;
			}
			
			uni.request({
				url: 'http://localhost:8088/api/weight-records',
				method: 'GET',
				header: {
					'Authorization': 'Bearer ' + token
				},
				success: (res) => {
					if (res.data.code === 0) {
						const records = res.data.data.records || [];
						records.sort((a, b) => new Date(b.recordedAt) - new Date(a.recordedAt));
						
						this.weightRecords = records.map((record, index) => {
							const prevRecord = records[index + 1];
							const change = prevRecord ? (record.weight - prevRecord.weight).toFixed(1) : 0;
							const changeText = change > 0 ? `+${change}kg` : `${change}kg`;
							const changeClass = change > 0 ? 'increase' : change < 0 ? 'decrease' : 'stable';
							
							const bmi = this.calculateBMI(record.weight);
							
							return {
								id: record.id,
								date: record.recordedAt.split('T')[0],
								weight: record.weight,
								bmi: bmi,
								change: changeText,
								changeClass: changeClass
							};
						});
						
						if (this.weightRecords.length > 0) {
							this.userInfo.weight = this.weightRecords[0].weight;
						}
					}
				}
			});
		},
		getCurrentDate() {
			const now = new Date();
			return now.getFullYear() + '-' + 
				(now.getMonth() + 1).toString().padStart(2, '0') + '-' + 
				now.getDate().toString().padStart(2, '0');
		}
	}
};
</script>

<style scoped>
	/* 定义颜色变量 */
	:root {
		--primary-color: #4A89FF;
		--success-color: #52c41a;
		--warning-color: #faad14;
		--danger-color: #f5222d;
		--background-color: #f7f8f9;
		--card-background: #ffffff;
		--text-color-primary: #333333;
		--text-color-secondary: #666666;
		--border-color: #ebebeb;
		--box-shadow: 0px 4px 12px rgba(99, 99, 99, 0.08);
	}

	.weightPage {
		width: 100%;
		min-height: 100vh;
		background-color: var(--background-color);
		padding-bottom: 3vh;
	}
	
	.header_box {
		width: 95%;
		margin: 0 auto;
		padding: 20px 0;
		display: flex;
		justify-content: space-between;
		align-items: center;
	}
	
	.page_title {
		font-size: 24px;
		font-weight: bold;
		color: var(--text-color-primary);
	}
	
	.add_weight_btn {
		background-color: var(--primary-color);
		color: white;
		padding: 8px 16px;
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
	
	.bmi_box {
		text-align: center;
	}
	
	.bmi_title {
		font-size: 18px;
		font-weight: bold;
		margin-bottom: 1em;
		color: var(--text-color-primary);
	}
	
	.bmi_content {
		display: flex;
		justify-content: center;
		align-items: baseline; /* Align to the bottom of flex container */
		margin-bottom: 1em;
	}
	
	.bmi_value {
		font-size: 48px;
		font-weight: bold;
		color: var(--primary-color);
		margin-right: 0.5em;
	}
	
	.unit {
		font-size: 14px;
		font-weight: normal;
		color: var(--text-color-secondary);
	}
	
	.bmi_status {
		font-size: 16px;
		padding: 0.3em 0.8em;
		border-radius: 999px; /* More rounded pill shape */
		color: white;
	}
	
	.bmi_status.normal {
		background-color: var(--success-color);
	}
	
	.bmi_status.underweight {
		background-color: var(--primary-color);
	}
	
	.bmi_status.overweight {
		background-color: var(--warning-color);
	}
	
	.bmi_status.obese {
		background-color: var(--danger-color);
	}
	
	.bmi_info {
		display: flex;
		justify-content: space-around;
		font-size: 14px;
		color: var(--text-color-secondary);
	}
	
	.records_title {
		font-size: 18px;
		font-weight: bold;
		padding-bottom: 15px;
		color: var(--text-color-primary);
		border-bottom: 1px solid var(--border-color);
	}
	
	.weight_records {
		padding-top: 15px;
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
	
	.record_date {
		width: 25%;
		font-size: 14px;
		color: var(--text-color-secondary);
	}
	
	.record_weight {
		width: 25%;
		font-size: 16px;
		font-weight: bold;
		color: var(--text-color-primary);
	}
	
	.record_bmi {
		width: 25%;
		font-size: 14px;
		color: var(--text-color-secondary);
	}
	
	.record_change {
		width: 25%;
		font-size: 14px;
		text-align: right;
	}
	
	.record_change.increase {
		color: var(--danger-color);
	}
	
	.record_change.decrease {
		color: var(--success-color);
	}
	
	.record_change.stable {
		color: var(--text-color-secondary);
	}
	
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
		width: 320px;
		max-width: 85%;
		background-color: var(--card-background);
		border-radius: 12px;
		padding: 24px;
	}
	
	.modal_title {
		font-size: 18px;
		font-weight: bold;
		text-align: center;
		margin-bottom: 24px;
		color: var(--text-color-primary);
	}
	
	.form_item {
		margin-bottom: 16px;
	}
	
	.form_label {
		display: block;
		font-size: 14px;
		color: var(--text-color-primary);
		margin-bottom: 8px;
	}
	
	.picker {
		width: 100%;
		height: 44px;
		line-height: 44px;
		padding-left: 12px;
		border: 1px solid var(--border-color);
		border-radius: 8px;
		box-sizing: border-box;
	}
	
	.form_input {
		width: 100%;
		height: 44px;
		padding: 0 12px;
		border: 1px solid var(--border-color);
		border-radius: 8px;
		box-sizing: border-box;
		font-size: 14px;
	}
	
	.modal_actions {
		display: flex;
		gap: 10px;
		margin-top: 24px;
	}
	
	.cancel_btn, .confirm_btn {
		flex: 1;
		height: 44px;
		line-height: 44px;
		border: none;
		border-radius: 8px;
		font-size: 16px;
	}
	
	.cancel_btn {
		background-color: #f0f0f0;
		color: var(--text-color-secondary);
	}
	
	.confirm_btn {
		background-color: var(--primary-color);
		color: white;
	}
</style>