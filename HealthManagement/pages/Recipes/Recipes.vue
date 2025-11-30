<template>
	<view class="recipes">
		<view class="header">
			<text class="title">食谱推荐</text>
		</view>
		
		<!-- 分类筛选 -->
		<view class="category-filter">
			<scroll-view class="category-scroll" scroll-x>
				<view class="category-list">
					<view 
						class="category-item" 
						:class="{ active: selectedCategoryId === null }"
						@click="selectCategory(null)"
					>
						全部
					</view>
					<view 
						class="category-item" 
						:class="{ active: selectedCategoryId === category.id }"
						v-for="category in categories" 
						:key="category.id"
						@click="selectCategory(category.id)"
					>
						{{ category.name }}
					</view>
				</view>
			</scroll-view>
		</view>
		
		<!-- 食谱列表 -->
		<view class="recipe-list">
			<view 
				class="recipe-item" 
				v-for="recipe in recipes" 
				:key="recipe.id"
				@click="viewRecipeDetail(recipe)"
			>
				<view class="recipe-image">
					<image src="/static/recipe-placeholder.png" mode="aspectFill" />
				</view>
				<view class="recipe-info">
					<view class="recipe-name">{{ recipe.name }}</view>
					<view class="recipe-desc">{{ recipe.description || '暂无描述' }}</view>
					<view class="recipe-meta">
						<text class="category-tag">{{ recipe.category?.name || '未分类' }}</text>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 空状态 -->
		<view v-if="recipes.length === 0 && !loading" class="empty-state">
			<text class="empty-text">暂无食谱</text>
		</view>
		
		<!-- 加载状态 -->
		<view v-if="loading" class="loading-state">
			<text class="loading-text">加载中...</text>
		</view>
	</view>
</template>

<script>
import apiConfig from '@/api/config.js';

export default {
	data() {
		return {
			recipes: [],
			categories: [],
			selectedCategoryId: null,
			loading: false
		};
	},
	onLoad() {
		this.loadCategories();
		this.loadRecipes();
	},
	methods: {
		// 加载分类
		loadCategories() {
			uni.request({
				url: apiConfig.API_CONFIG.BASE_URL + '/recipe-categories',
				method: 'GET',
				success: (res) => {
					if (res.data.code === 0) {
						this.categories = res.data.data || [];
					}
				},
				fail: () => {
					console.log('加载分类失败');
				}
			});
		},
		
		// 加载食谱
		loadRecipes() {
			this.loading = true;
			const url = this.selectedCategoryId 
				? `${apiConfig.API_CONFIG.BASE_URL}${apiConfig.API_CONFIG.RECIPES.BY_CATEGORY}/${this.selectedCategoryId}`
				: `${apiConfig.API_CONFIG.BASE_URL}${apiConfig.API_CONFIG.RECIPES.ENABLED}`;
			
			uni.request({
				url: url,
				method: 'GET',
				success: (res) => {
					if (res.data.code === 0) {
						this.recipes = res.data.data || [];
					}
				},
				fail: () => {
					console.log('加载食谱失败');
				},
				complete: () => {
					this.loading = false;
				}
			});
		},
		
		// 选择分类
		selectCategory(categoryId) {
			this.selectedCategoryId = categoryId;
			this.loadRecipes();
		},
		
		// 查看食谱详情
		viewRecipeDetail(recipe) {
			uni.navigateTo({
				url: `/pages/RecipeDetail/RecipeDetail?recipeId=${recipe.id}`
			});
		}
	}
};
</script>

<style scoped>
.recipes {
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

.category-filter {
	margin-bottom: 20px;
}

.category-scroll {
	white-space: nowrap;
}

.category-list {
	display: flex;
	gap: 12px;
	padding: 0 20px;
}

.category-item {
	padding: 8px 16px;
	background: #fff;
	border-radius: 20px;
	font-size: 14px;
	color: #666;
	border: 1px solid #e0e0e0;
	white-space: nowrap;
	transition: all 0.3s;
}

.category-item.active {
	background: #F5296E;
	color: #fff;
	border-color: #F5296E;
}

.recipe-list {
	display: flex;
	flex-direction: column;
	gap: 16px;
}

.recipe-item {
	background: #fff;
	border-radius: 12px;
	padding: 16px;
	box-shadow: 0 2px 8px rgba(0,0,0,0.1);
	display: flex;
	gap: 12px;
}

.recipe-image {
	width: 80px;
	height: 80px;
	border-radius: 8px;
	overflow: hidden;
	flex-shrink: 0;
}

.recipe-image image {
	width: 100%;
	height: 100%;
}

.recipe-info {
	flex: 1;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}

.recipe-name {
	font-size: 16px;
	font-weight: bold;
	color: #333;
	margin-bottom: 4px;
}

.recipe-desc {
	font-size: 14px;
	color: #666;
	line-height: 1.4;
	margin-bottom: 8px;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
}

.recipe-meta {
	display: flex;
	align-items: center;
	gap: 8px;
}

.category-tag {
	padding: 2px 8px;
	background: #f0f0f0;
	border-radius: 12px;
	font-size: 12px;
	color: #666;
}

.empty-state, .loading-state {
	text-align: center;
	padding: 60px 20px;
}

.empty-text, .loading-text {
	font-size: 16px;
	color: #999;
}
</style>


