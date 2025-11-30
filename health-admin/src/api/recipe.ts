import request from './request'

export interface Recipe {
  id?: number
  categoryId?: number
  name: string
  description?: string
  enabled: number
  createdBy?: number
  createdAt?: string
  updatedAt?: string
  category?: {
    id: number
    name: string
  }
  items?: RecipeItem[]
}

export interface RecipeItem {
  id?: number
  recipeId: number
  foodId: number
  quantity: number
  quantityUnit: string
  createdAt?: string
  food?: {
    id: number
    name: string
    caloriesPer100: number
  }
}

export interface RecipeCategory {
  id: number
  name: string
  parentId?: number
  sort: number
  createdAt: string
  updatedAt: string
}

export interface RecipeQueryParams {
  page?: number
  size?: number
  keyword?: string
  categoryId?: number
  enabled?: number
}

export interface RecipeListResult {
  records: Recipe[]
  total: number
  page: number
  size: number
}

// 获取食谱列表
export function getRecipeList(params: RecipeQueryParams) {
  return request.get<RecipeListResult>('/admin/recipes', { params })
}

// 获取启用的食谱列表
export function getEnabledRecipes() {
  return request.get<Recipe[]>('/admin/recipes/enabled')
}

// 根据分类获取食谱
export function getRecipesByCategory(categoryId: number) {
  return request.get<Recipe[]>(`/admin/recipes/category/${categoryId}`)
}

// 获取食谱详情
export function getRecipeDetail(id: number) {
  return request.get<Recipe>(`/admin/recipes/${id}`)
}

// 获取食谱明细
export function getRecipeItems(id: number) {
  return request.get<RecipeItem[]>(`/admin/recipes/${id}/items`)
}

// 创建食谱
export function createRecipe(data: Partial<Recipe>) {
  return request.post<Recipe>('/admin/recipes', data)
}

// 更新食谱
export function updateRecipe(id: number, data: Partial<Recipe>) {
  return request.put<Recipe>(`/admin/recipes/${id}`, data)
}

// 删除食谱
export function deleteRecipe(id: number) {
  return request.delete(`/admin/recipes/${id}`)
}

// 获取食谱分类列表
export function getRecipeCategories() {
  return request.get<RecipeCategory[]>('/admin/recipe-categories')
}

// 获取顶级分类
export function getTopLevelCategories() {
  return request.get<RecipeCategory[]>('/admin/recipe-categories/top-level')
}

// 获取子分类
export function getCategoryChildren(parentId: number) {
  return request.get<RecipeCategory[]>(`/admin/recipe-categories/${parentId}/children`)
}

// 创建食谱分类
export function createRecipeCategory(data: Partial<RecipeCategory>) {
  return request.post<RecipeCategory>('/admin/recipe-categories', data)
}

// 更新食谱分类
export function updateRecipeCategory(id: number, data: Partial<RecipeCategory>) {
  return request.put<RecipeCategory>(`/admin/recipe-categories/${id}`, data)
}

// 删除食谱分类
export function deleteRecipeCategory(id: number) {
  return request.delete(`/admin/recipe-categories/${id}`)
}

// 保存食谱食材
export function saveRecipeItems(recipeId: number, items: Partial<RecipeItem>[]) {
  return request.post(`/admin/recipes/${recipeId}/items`, items)
}

// 更新食谱食材
export function updateRecipeItems(recipeId: number, items: Partial<RecipeItem>[]) {
  return request.put(`/admin/recipes/${recipeId}/items`, items)
}

// 删除食谱食材
export function deleteRecipeItem(recipeId: number, itemId: number) {
  return request.delete(`/admin/recipes/${recipeId}/items/${itemId}`)
}

// 上传食谱图片
export function uploadRecipeImage(file: File) {
  const formData = new FormData()
  formData.append('image', file)
  return request.post<{ imageUrl: string; message: string }>('/admin/recipes/upload-image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取食谱统计
export function getRecipeStats() {
  return request.get('/admin/recipes/stats')
}


