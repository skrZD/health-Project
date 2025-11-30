import request from './request'

export interface Food {
  id?: number
  categoryId?: number
  name: string
  alias?: string
  unit: string
  caloriesPer100: number
  proteinGPer100?: number
  fatGPer100?: number
  carbsGPer100?: number
  sodiumMgPer100?: number
  enabled: number
  createdAt?: string
  updatedAt?: string
}

export interface FoodCategory {
  id: number
  name: string
  parentId?: number
  sort: number
  createdAt: string
  updatedAt: string
}

export interface FoodQueryParams {
  page?: number
  size?: number
  keyword?: string
  categoryId?: number
  enabled?: number
}

export interface FoodListResult {
  records: Food[]
  total: number
  page: number
  size: number
}

// 获取食物列表
export function getFoodList(params: FoodQueryParams) {
  return request.get<FoodListResult>('/admin/foods', { params })
}

// 获取食物详情
export function getFoodDetail(id: number) {
  return request.get<Food>(`/admin/foods/${id}`)
}

// 创建食物
export function createFood(data: Partial<Food>) {
  return request.post<Food>('/admin/foods', data)
}

// 更新食物
export function updateFood(id: number, data: Partial<Food>) {
  return request.put<Food>(`/admin/foods/${id}`, data)
}

// 删除食物
export function deleteFood(id: number) {
  return request.delete(`/admin/foods/${id}`)
}

// 获取食物分类列表
export function getFoodCategories() {
  return request.get<FoodCategory[]>('/admin/food-categories')
}

// 创建食物分类
export function createFoodCategory(data: Partial<FoodCategory>) {
  return request.post<FoodCategory>('/admin/food-categories', data)
}

// 更新食物分类
export function updateFoodCategory(id: number, data: Partial<FoodCategory>) {
  return request.put<FoodCategory>(`/admin/food-categories/${id}`, data)
}

// 删除食物分类
export function deleteFoodCategory(id: number) {
  return request.delete(`/admin/food-categories/${id}`)
}

// 获取食物统计
export function getFoodStats() {
  return request.get('/admin/foods/stats')
}

