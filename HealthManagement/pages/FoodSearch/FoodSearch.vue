<template>
	<view class="foodSearch">
		<view class="search_bar">
			<input class="search_input" v-model="keyword" placeholder="搜索食物名称" confirm-type="search" @confirm="search"/>
			<button class="search_btn" @click="search">搜索</button>
		</view>
		<view class="category_bar">
			<scroll-view scroll-x class="cat_scroll">
				<view class="cat_item" :class="{active: activeCat === c.id}" v-for="c in categories" :key="c.id" @click="selectCat(c.id)">{{c.name}}</view>
			</scroll-view>
		</view>
		<view class="list_box">
			<view class="food_item" v-for="f in foods" :key="f.id" @click="chooseFood(f)">
				<view class="food_name">{{f.name}}</view>
				<view class="food_kcal">{{f.caloriesPer100}} kcal/100g</view>
			</view>
			<view v-if="foods.length===0" class="empty">暂无数据</view>
		</view>
	</view>
</template>

<script>
export default {
	data(){
		return {
			keyword: '',
			activeCat: null,
			categories: [
				{ id: 0, name: '全部' },
				{ id: 1, name: '主食' },
				{ id: 2, name: '肉蛋奶' },
				{ id: 3, name: '蔬果' }
			],
			foods: []
		};
	},
	onLoad(){
		this.search();
	},
	methods:{
		selectCat(id){
			this.activeCat = id;
			this.search();
		},
		search(){
			// 调用后端API搜索食物
			const params = {
				q: this.keyword,
				page: 1,
				size: 50
			};
			
			// 只有当activeCat不为0时才添加categoryId参数
			if (this.activeCat !== 0) {
				params.categoryId = this.activeCat;
			}
			
			uni.request({
				url: 'http://localhost:8088/api/foods/search',
				method: 'GET',
				data: params,
				success: (res) => {
					if (res.data.code === 0) {
						this.foods = res.data.data.records || [];
					} else {
						uni.showToast({ title: '搜索失败', icon: 'none' });
					}
				},
				fail: () => {
					// 如果API调用失败，使用mock数据
					const all = [
						{ id: 101, name: '鸡胸肉', caloriesPer100: 165 },
						{ id: 102, name: '米饭', caloriesPer100: 116 },
						{ id: 103, name: '西兰花', caloriesPer100: 33 }
					];
					this.foods = all.filter(x => !this.keyword || x.name.includes(this.keyword));
				}
			});
		},
		chooseFood(food){
			// 选择后返回上一页并带参数
			const eventChannel = this.getOpenerEventChannel();
			// 统一字段名，将caloriesPer100转换为calories_per_100
			const selectedFood = {
				...food,
				calories_per_100: food.caloriesPer100
			};
			eventChannel.emit('foodSelected', selectedFood);
			uni.navigateBack();
		}
	}
}
</script>

<style scoped>
.foodSearch{ padding: 10px; }
.search_bar{ display:flex; gap:8px; }
.search_input{ flex:1; height:36px; border:1px solid #eee; border-radius:8px; padding:0 10px; }
.search_btn{ background:#4A89FF; color:#fff; border:none; border-radius:8px; padding:0 14px; }
.cat_scroll{ white-space:nowrap; padding:10px 0; }
.cat_item{ display:inline-block; padding:6px 12px; margin-right:8px; background:#f5f5f5; border-radius:14px; }
.cat_item.active{ background:#4A89FF; color:#fff; }
.list_box{ background:#fff; border-radius:12px; box-shadow:0 2px 8px rgba(0,0,0,.06); }
.food_item{ display:flex; justify-content:space-between; padding:12px 14px; border-bottom:1px solid #f0f0f0; }
.food_item:last-child{ border-bottom:none; }
.food_name{ font-weight:bold; }
.food_kcal{ color:#666; }
.empty{ text-align:center; color:#999; padding:20px; }
</style>


