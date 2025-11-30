<template>
	<view class="recipeDetail">
		<view class="header">
			<text class="title">{{ recipe.name }}</text>
		</view>
		
		<view class="recipe-info">
			<view class="info-row">
				<text class="label">分类：</text>
				<text class="value">{{ recipe.category?.name || '未分类' }}</text>
			</view>
			<view class="info-row" v-if="recipe.description">
				<text class="label">描述：</text>
				<text class="value">{{ recipe.description }}</text>
			</view>
		</view>
		
		<view class="ingredients-section">
			<view class="section-title">食材明细</view>
			<view class="ingredients-list">
				<view 
					class="ingredient-item" 
					v-for="item in recipeItems" 
					:key="item.id"
				>
					<view class="ingredient-name">{{ item.foodName || '未知食材' }}</view>
					<view class="ingredient-quantity">
						{{ item.quantity }} {{ item.quantityUnit }}
					</view>
				</view>
			</view>
		</view>
		
		<view class="actions">
			<button class="btn-use" @click="useRecipe">使用此食谱</button>
		</view>
	</view>
</template>

<script>
import apiConfig from '@/api/config.js';

export default {
	data() {
		return {
			recipe: {
				name: '',
				description: '',
				category: null
			},
			recipeItems: []
		};
	},
	onLoad(options) {
		if (options.recipeId) {
			this.loadRecipeDetail(options.recipeId);
		}
	},
	methods: {
		// 加载食谱详情
		loadRecipeDetail(recipeId) {
			uni.request({
				url: `${apiConfig.API_CONFIG.BASE_URL}${apiConfig.API_CONFIG.RECIPES.DETAIL_WITH_ITEMS}/${recipeId}/detail`,
				method: 'GET',
				success: (res) => {
					if (res.data.code === 0) {
						const data = res.data.data;
						this.recipe = data.recipe;
						this.recipeItems = data.items || [];
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
		
		// 加载食谱明细（备用方法）
		loadRecipeItems(recipeId) {
			uni.request({
				url: `${apiConfig.API_CONFIG.BASE_URL}${apiConfig.API_CONFIG.RECIPES.ITEMS}/${recipeId}`,
				method: 'GET',
				success: (res) => {
					if (res.data.code === 0) {
						this.recipeItems = res.data.data || [];
					}
				},
				fail: () => {
					console.log('加载食谱明细失败');
				}
			});
		},
		
		// 使用食谱
		useRecipe() {
			// 将食谱信息传递给饮食记录页面
			uni.navigateTo({
				url: `/pages/DietRecordDetail/DietRecordDetail?recipeId=${this.recipe.id}&recipeName=${encodeURIComponent(this.recipe.name)}`
			});
		}
	}
};
</script>

<style scoped>
.recipeDetail {
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

.recipe-info {
	background: #fff;
	border-radius: 12px;
	padding: 20px;
	margin-bottom: 20px;
	box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.info-row {
	display: flex;
	margin-bottom: 12px;
}

.info-row:last-child {
	margin-bottom: 0;
}

.label {
	font-size: 16px;
	color: #666;
	width: 80px;
	flex-shrink: 0;
}

.value {
	font-size: 16px;
	color: #333;
	flex: 1;
}

.ingredients-section {
	background: #fff;
	border-radius: 12px;
	padding: 20px;
	margin-bottom: 20px;
	box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.section-title {
	font-size: 18px;
	font-weight: bold;
	color: #333;
	margin-bottom: 16px;
	border-bottom: 2px solid #f0f0f0;
	padding-bottom: 8px;
}

.ingredients-list {
	display: flex;
	flex-direction: column;
	gap: 12px;
}

.ingredient-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 12px;
	background: #f8f8f8;
	border-radius: 8px;
}

.ingredient-name {
	font-size: 16px;
	color: #333;
	flex: 1;
}

.ingredient-quantity {
	font-size: 14px;
	color: #666;
	font-weight: bold;
}

.actions {
	padding: 20px 0;
}

.btn-use {
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
