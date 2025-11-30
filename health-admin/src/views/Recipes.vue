<template>
  <div class="recipes-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">
          <span class="title-icon">🍽️</span>
          食谱管理
        </h1>
        <p class="page-subtitle">管理和维护健康食谱信息</p>
      </div>
      <div class="header-right">
        <a-button type="primary" @click="showCreateModal" size="large">
          <template #icon>
            <PlusOutlined />
          </template>
          新增食谱
        </a-button>
      </div>
    </div>

    <!-- 搜索筛选 -->
    <a-card class="search-card" :bordered="false">
      <template #title>
        <div class="card-title">
          <span class="card-icon">🔍</span>
          搜索筛选
        </div>
      </template>
      <a-form
        :model="searchParams"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 18 }"
        @finish="handleSearch"
      >
        <a-row :gutter="[16, 16]">
          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <a-form-item label="关键词" name="keyword">
              <a-input
                v-model:value="searchParams.keyword"
                placeholder="搜索食谱名称或描述"
                allow-clear
                prefix="🔍"
              />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <a-form-item label="分类" name="categoryId">
              <a-select
                v-model:value="searchParams.categoryId"
                placeholder="选择分类"
                allow-clear
                style="width: 100%"
              >
                <a-select-option
                  v-for="category in categories"
                  :key="category.id"
                  :value="category.id"
                >
                  {{ category.name }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <a-form-item label="状态" name="enabled">
              <a-select
                v-model:value="searchParams.enabled"
                placeholder="选择状态"
                allow-clear
                style="width: 100%"
              >
                <a-select-option :value="1">✅ 启用</a-select-option>
                <a-select-option :value="0">❌ 禁用</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <a-form-item>
              <a-space>
                <a-button type="primary" html-type="submit" :loading="loading">
                  <template #icon>
                    <SearchOutlined />
                  </template>
                  搜索
                </a-button>
                <a-button @click="handleReset">
                  <template #icon>
                    <span>🔄</span>
                  </template>
                  重置
                </a-button>
              </a-space>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-card>

    <!-- 食谱卡片网格 -->
    <a-card class="recipes-card" :bordered="false">
      <template #title>
        <div class="card-title">
          <span class="card-icon">🍽️</span>
          食谱列表
        </div>
      </template>
      <template #extra>
        <a-tooltip title="刷新数据">
          <a-button
            type="text"
            @click="loadRecipes"
            :loading="loading"
            class="refresh-btn"
          >
            <template #icon>
              <span>🔄</span>
            </template>
          </a-button>
        </a-tooltip>
      </template>
      
      <div v-if="loading" class="loading-container">
        <a-spin size="large" />
      </div>
      
      <div v-else-if="recipes.length === 0" class="empty-container">
        <a-empty description="暂无食谱数据" />
      </div>
      
      <div v-else class="recipes-grid">
        <div
          v-for="recipe in recipes"
          :key="recipe.id"
          class="recipe-card"
          @click="handleViewRecipe(recipe)"
        >
          <div class="recipe-image">
            <img 
              v-if="recipe.imageUrl" 
              :src="getImageUrl(recipe.imageUrl)" 
              :alt="recipe.name"
              class="recipe-img"
            />
            <div v-else class="recipe-placeholder">
              <span class="placeholder-icon">🍽️</span>
            </div>
            <div class="recipe-status">
              <a-tag :color="recipe.enabled ? 'green' : 'red'" class="status-tag">
                {{ recipe.enabled ? '启用' : '禁用' }}
              </a-tag>
            </div>
          </div>
          
          <div class="recipe-content">
            <div class="recipe-header">
              <h3 class="recipe-name">{{ recipe.name }}</h3>
              <span class="recipe-category">{{ recipe.category?.name || '未分类' }}</span>
            </div>
            
            <div class="recipe-description">
              {{ recipe.description || '暂无描述' }}
            </div>
            
            <div class="recipe-meta">
              <div class="meta-item">
                <span class="meta-icon">📅</span>
                <span class="meta-text">{{ formatDate(recipe.createdAt) }}</span>
              </div>
              <div class="meta-item" v-if="recipe.difficultyLevel">
                <span class="meta-icon">⚡</span>
                <span class="meta-text">{{ recipe.difficultyLevel }}</span>
              </div>
              <div class="meta-item" v-if="recipe.cookingTime">
                <span class="meta-icon">⏱️</span>
                <span class="meta-text">{{ recipe.cookingTime }}分钟</span>
              </div>
            </div>
          </div>
          
          <div class="recipe-actions">
            <a-button type="text" size="small" @click.stop="handleViewRecipe(recipe)">
              <template #icon>
                <span>👁️</span>
              </template>
              查看
            </a-button>
            <a-button type="text" size="small" @click.stop="handleEdit(recipe)">
              <template #icon>
                <span>✏️</span>
              </template>
              编辑
            </a-button>
            <a-button type="text" size="small" @click.stop="handleViewItems(recipe)">
              <template #icon>
                <span>📋</span>
              </template>
              食材
            </a-button>
            <a-popconfirm
              title="确定要删除这个食谱吗？"
              @confirm="handleDelete(recipe.id)"
            >
              <a-button type="text" size="small" danger @click.stop>
                <template #icon>
                  <span>🗑️</span>
                </template>
                删除
              </a-button>
            </a-popconfirm>
          </div>
        </div>
      </div>
    </a-card>

    <!-- 食谱编辑弹窗 -->
    <a-modal
        v-model:open="modalVisible"
        :title="modalTitle"
        width="1000px"
        @ok="handleModalOk"
        @cancel="handleModalCancel"
        class="recipe-modal"
    >
      <template #title>
        <div class="modal-title">
          <span class="modal-icon">🍽️</span>
          {{ modalTitle }}
        </div>
      </template>

      <a-form
          ref="formRef"
          :model="formData"
          :rules="formRules"
          :label-col="{ span: 4 }"
          :wrapper-col="{ span: 20 }"
      >
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="食谱名称" name="name">
              <a-input v-model:value="formData.name" placeholder="请输入食谱名称" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="分类" name="categoryId">
              <a-select
                  v-model:value="formData.categoryId"
                  placeholder="请选择分类"
                  style="width: 100%"
              >
                <a-select-option
                    v-for="category in categories"
                    :key="category.id"
                    :value="category.id"
                >
                  {{ category.name }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="难度等级" name="difficultyLevel">
              <a-select
                  v-model:value="formData.difficultyLevel"
                  placeholder="请选择难度等级"
                  style="width: 100%"
              >
                <a-select-option value="简单">简单</a-select-option>
                <a-select-option value="中等">中等</a-select-option>
                <a-select-option value="困难">困难</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="烹饪时间" name="cookingTime">
              <a-input-number
                  v-model:value="formData.cookingTime"
                  placeholder="请输入烹饪时间"
                  :min="1"
                  :max="300"
                  style="width: 100%"
                  addon-after="分钟"
              />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="份数" name="servings">
              <a-input-number
                  v-model:value="formData.servings"
                  placeholder="请输入份数"
                  :min="1"
                  :max="20"
                  style="width: 100%"
                  addon-after="人份"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="状态" name="enabled">
              <a-radio-group v-model:value="formData.enabled">
                <a-radio :value="1">✅ 启用</a-radio>
                <a-radio :value="0">❌ 禁用</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-form-item label="食谱图片" name="imageUrl">
          <a-upload
              v-model:file-list="imageFileList"
              :before-upload="beforeUpload"
              :remove="handleRemoveImage"
              list-type="picture-card"
              :max-count="1"
          >
            <div v-if="imageFileList.length < 1">
              <plus-outlined />
              <div style="margin-top: 8px">上传图片</div>
            </div>
          </a-upload>
        </a-form-item>
        
        <a-form-item label="描述" name="description">
          <a-textarea
              v-model:value="formData.description"
              placeholder="请输入食谱描述"
              :rows="3"
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 食谱详情弹窗 -->
    <a-modal
        v-model:open="recipeDetailVisible"
        :title="`${currentRecipe?.name || '食谱'}详情`"
        width="900px"
        class="recipe-detail-modal"
        :footer="null"
    >
      <template #title>
        <div class="modal-title">
          <span class="modal-icon">🍽️</span>
          {{ currentRecipe?.name || '食谱' }}详情
        </div>
      </template>

      <div v-if="currentRecipe" class="recipe-detail-content">
        <!-- 食谱基本信息 -->
        <div class="recipe-basic-info">
          <div class="recipe-header">
            <div class="recipe-image-section">
              <img 
                v-if="currentRecipe.imageUrl" 
                :src="getImageUrl(currentRecipe.imageUrl)" 
                :alt="currentRecipe.name"
                class="recipe-detail-image"
              />
              <div v-else class="recipe-detail-placeholder">
                <span class="placeholder-icon">🍽️</span>
              </div>
            </div>
            
            <div class="recipe-info-section">
              <h2 class="recipe-detail-name">{{ currentRecipe.name }}</h2>
              <div class="recipe-meta-info">
                <div class="meta-row">
                  <span class="meta-label">分类：</span>
                  <span class="meta-value">{{ currentRecipe.category?.name || '未分类' }}</span>
                </div>
                <div class="meta-row" v-if="currentRecipe.difficultyLevel">
                  <span class="meta-label">难度：</span>
                  <span class="meta-value">{{ currentRecipe.difficultyLevel }}</span>
                </div>
                <div class="meta-row" v-if="currentRecipe.cookingTime">
                  <span class="meta-label">烹饪时间：</span>
                  <span class="meta-value">{{ currentRecipe.cookingTime }}分钟</span>
                </div>
                <div class="meta-row" v-if="currentRecipe.servings">
                  <span class="meta-label">份数：</span>
                  <span class="meta-value">{{ currentRecipe.servings }}人份</span>
                </div>
                <div class="meta-row">
                  <span class="meta-label">状态：</span>
                  <a-tag :color="currentRecipe.enabled ? 'green' : 'red'">
                    {{ currentRecipe.enabled ? '启用' : '禁用' }}
                  </a-tag>
                </div>
              </div>
            </div>
          </div>
          
          <div class="recipe-description-section" v-if="currentRecipe.description">
            <h3 class="section-title">📝 食谱描述</h3>
            <p class="recipe-description-text">{{ currentRecipe.description }}</p>
          </div>
        </div>

        <!-- 食材明细 -->
        <div class="ingredients-section" v-if="currentRecipeItems.length > 0">
          <h3 class="section-title">🥘 食材明细</h3>
          <div class="ingredients-list">
            <div 
              v-for="(item, index) in currentRecipeItems" 
              :key="index"
              class="ingredient-item"
            >
              <div class="ingredient-info">
                <span class="ingredient-name">{{ item.foodName || '未知食材' }}</span>
                <span class="ingredient-quantity">{{ item.quantity }} {{ item.quantityUnit }}</span>
              </div>
              <div class="ingredient-calories" v-if="item.calories">
                {{ item.calories.toFixed(1) }}千卡
              </div>
            </div>
          </div>
          
          <!-- 总热量统计 -->
          <div class="total-calories">
            <span class="total-label">总热量：</span>
            <span class="total-value">
              {{ currentRecipeItems.reduce((sum, item) => sum + (item.calories || 0), 0).toFixed(1) }}千卡
            </span>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else class="empty-ingredients">
          <a-empty description="暂无食材信息" />
        </div>
      </div>
    </a-modal>

    <!-- 食材明细弹窗 -->
    <a-modal
        v-model:open="itemsModalVisible"
        :title="`${currentRecipe?.name || '食谱'}的食材明细`"
        width="800px"
        class="ingredients-modal"
    >
      <template #title>
        <div class="modal-title">
          <span class="modal-icon">📋</span>
          {{ currentRecipe?.name || '食谱' }}的食材明细
        </div>
      </template>

      <div v-if="currentRecipe">
        <div class="recipe-info">
          <div class="recipe-basic-info">
            <h3>{{ currentRecipe.name }}</h3>
            <p class="recipe-desc">{{ currentRecipe.description }}</p>
          </div>
          
          <div class="ingredients-actions">
            <a-button type="primary" @click="handleAddIngredient">
              <template #icon>
                <PlusOutlined />
              </template>
              添加食材
            </a-button>
          </div>
        </div>
        
        <a-table
            :columns="itemColumns"
            :data-source="currentRecipeItems"
            :pagination="false"
            size="small"
            class="ingredients-table"
        >
          <template #bodyCell="{ column, record, index }">
            <template v-if="column.key === 'foodName'">
              <a-select
                v-model:value="record.foodId"
                placeholder="选择食材"
                style="width: 100%"
                @change="handleFoodChange(record, index)"
                show-search
                :filter-option="(input, option) => 
                  option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0"
              >
                <a-select-option
                  v-for="food in availableFoods"
                  :key="food.id"
                  :value="food.id"
                >
                  {{ food.name }}
                </a-select-option>
              </a-select>
            </template>
            <template v-else-if="column.key === 'quantity'">
              <a-input-number
                v-model:value="record.quantity"
                :min="0.1"
                :max="1000"
                :precision="1"
                style="width: 100%"
                @change="handleQuantityChange(record, index)"
              />
            </template>
            <template v-else-if="column.key === 'quantityUnit'">
              <a-select
                v-model:value="record.quantityUnit"
                style="width: 100%"
                @change="handleUnitChange(record, index)"
              >
                <a-select-option value="g">克</a-select-option>
                <a-select-option value="ml">毫升</a-select-option>
                <a-select-option value="个">个</a-select-option>
                <a-select-option value="片">片</a-select-option>
                <a-select-option value="杯">杯</a-select-option>
                <a-select-option value="勺">勺</a-select-option>
                <a-select-option value="份">份</a-select-option>
              </a-select>
            </template>
            <template v-else-if="column.key === 'calories'">
              <span class="calories-display">{{ record.calories || 0 }}千卡</span>
            </template>
            <template v-else-if="column.key === 'actions'">
              <a-button type="text" size="small" danger @click="handleRemoveIngredient(index)">
                <template #icon>
                  <span>🗑️</span>
                </template>
                删除
              </a-button>
            </template>
          </template>
        </a-table>
      </div>
      
      <template #footer>
        <a-space>
          <a-button @click="itemsModalVisible = false">取消</a-button>
          <a-button type="primary" @click="handleSaveIngredients">保存食材</a-button>
        </a-space>
      </template>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { message } from 'ant-design-vue';
import { PlusOutlined, SearchOutlined } from '@ant-design/icons-vue';
import dayjs from 'dayjs';
import {
  getRecipeList,
  createRecipe,
  updateRecipe,
  deleteRecipe,
  getRecipeItems,
  getRecipeCategories,
  saveRecipeItems,
  updateRecipeItems,
  uploadRecipeImage,
  type Recipe,
  type RecipeItem,
  type RecipeCategory
} from '@/api/recipe';
import { getFoodList, type Food } from '@/api/food';

// 响应式数据
const loading = ref(false);
const recipes = ref<Recipe[]>([]);
const categories = ref<RecipeCategory[]>([]);
const availableFoods = ref<Food[]>([]);
const currentRecipeItems = ref<RecipeItem[]>([]);
const imageFileList = ref([]);

// 搜索参数
const searchParams = reactive({
  keyword: '',
  categoryId: undefined,
  enabled: undefined
});

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`
});

// 食材明细表格列配置
const itemColumns = [
  {
    title: '食材名称',
    dataIndex: 'foodName',
    key: 'foodName',
    width: 200
  },
  {
    title: '数量',
    dataIndex: 'quantity',
    key: 'quantity',
    width: 120
  },
  {
    title: '单位',
    dataIndex: 'quantityUnit',
    key: 'quantityUnit',
    width: 100
  },
  {
    title: '热量',
    dataIndex: 'calories',
    key: 'calories',
    width: 100
  },
  {
    title: '操作',
    key: 'actions',
    width: 80
  }
];

// 弹窗相关
const modalVisible = ref(false);
const itemsModalVisible = ref(false);
const recipeDetailVisible = ref(false);
const modalTitle = computed(() => formData.id ? '编辑食谱' : '新增食谱');
const currentRecipe = ref<Recipe | null>(null);

// 表单相关
const formRef = ref();
const formData = reactive<Partial<Recipe>>({
  name: '',
  categoryId: undefined,
  description: '',
  enabled: 1,
  difficultyLevel: '简单',
  cookingTime: undefined,
  servings: 1,
  imageUrl: ''
});

const formRules = {
  name: [{ required: true, message: '请输入食谱名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }]
};

// 方法
const loadRecipes = async () => {
  loading.value = true;
  try {
    const params = {
      page: pagination.current,
      size: pagination.pageSize,
      ...searchParams
    };
    const result = await getRecipeList(params);
    recipes.value = result.data.records;
    pagination.total = result.data.total;
  } catch (error) {
    message.error('加载食谱列表失败');
  } finally {
    loading.value = false;
  }
};

const loadCategories = async () => {
  try {
    const result = await getRecipeCategories();
    categories.value = result.data;
  } catch (error) {
    message.error('加载分类列表失败');
  }
};

const loadFoods = async () => {
  try {
    const result = await getFoodList({ page: 1, size: 1000 });
    availableFoods.value = result.data.records;
  } catch (error) {
    message.error('加载食材列表失败');
  }
};

const handleSearch = () => {
  pagination.current = 1;
  loadRecipes();
};

const handleReset = () => {
  Object.assign(searchParams, {
    keyword: '',
    categoryId: undefined,
    enabled: undefined
  });
  loadRecipes();
};

const handleTableChange = (pag: any) => {
  pagination.current = pag.current;
  pagination.pageSize = pag.pageSize;
  loadRecipes();
};

const showCreateModal = () => {
  formData.id = undefined;
  formData.name = '';
  formData.categoryId = undefined;
  formData.description = '';
  formData.enabled = 1;
  formData.difficultyLevel = '简单';
  formData.cookingTime = undefined;
  formData.servings = 1;
  formData.imageUrl = '';
  imageFileList.value = [];
  modalVisible.value = true;
};

const handleEdit = (record: Recipe) => {
  formData.id = record.id;
  formData.name = record.name;
  formData.categoryId = record.categoryId;
  formData.description = record.description;
  formData.enabled = record.enabled;
  formData.difficultyLevel = record.difficultyLevel || '简单';
  formData.cookingTime = record.cookingTime;
  formData.servings = record.servings || 1;
  formData.imageUrl = record.imageUrl || '';
  
  // 设置图片文件列表
  if (record.imageUrl) {
    imageFileList.value = [{
      uid: '-1',
      name: 'image.jpg',
      status: 'done',
      url: record.imageUrl,
    }];
  } else {
    imageFileList.value = [];
  }
  
  modalVisible.value = true;
};

const handleViewRecipe = async (record: Recipe) => {
  // 查看食谱详情
  currentRecipe.value = record;
  try {
    const result = await getRecipeItems(record.id!);
    currentRecipeItems.value = result.data;
    recipeDetailVisible.value = true;
  } catch (error) {
    message.error('加载食谱详情失败');
  }
};

const handleViewItems = async (record: Recipe) => {
  currentRecipe.value = record;
  try {
    // 先加载食材列表
    await loadFoods();
    // 再加载食谱食材数据
    await loadRecipeItems(record.id!);
    itemsModalVisible.value = true;
  } catch (error) {
    message.error('加载食谱明细失败');
  }
};

const handleDelete = async (id: number) => {
  try {
    await deleteRecipe(id);
    message.success('删除成功');
    loadRecipes();
  } catch (error) {
    message.error('删除失败');
  }
};

const handleModalOk = async () => {
  try {
    await formRef.value.validate();

    if (formData.id) {
      await updateRecipe(formData.id, formData);
      message.success('更新成功');
    } else {
      await createRecipe(formData);
      message.success('创建成功');
    }

    modalVisible.value = false;
    loadRecipes();
  } catch (error) {
    message.error(formData.id ? '更新失败' : '创建失败');
  }
};

const handleModalCancel = () => {
  modalVisible.value = false;
  formRef.value?.resetFields();
  imageFileList.value = [];
};

// 图片上传相关
const beforeUpload = async (file: any) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
  if (!isJpgOrPng) {
    message.error('只能上传 JPG/PNG 格式的图片!');
    return false;
  }
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    message.error('图片大小不能超过 2MB!');
    return false;
  }
  
  // 调用后端接口上传图片
  try {
    const response = await uploadRecipeImage(file);
    if (response.data) {
      formData.imageUrl = response.data.imageUrl;
      message.success('图片上传成功');
    }
  } catch (error) {
    message.error('图片上传失败');
    console.error('Upload error:', error);
  }
  
  return false; // 阻止自动上传
};

const handleRemoveImage = () => {
  imageFileList.value = [];
  formData.imageUrl = '';
};

// 食材管理相关
const handleAddIngredient = () => {
  currentRecipeItems.value.push({
    recipeId: currentRecipe.value?.id || 0,
    foodId: undefined,
    foodName: '',
    quantity: 1,
    quantityUnit: 'g',
    calories: 0
  });
};

const handleRemoveIngredient = (index: number) => {
  currentRecipeItems.value.splice(index, 1);
};

const handleFoodChange = (record: any, index: number) => {
  const food = availableFoods.value.find(f => f.id === record.foodId);
  if (food) {
    record.foodName = food.name;
    record.calories = (food.caloriesPer100 * record.quantity) / 100;
  }
};

// 确保食材名称正确显示
const ensureFoodNames = () => {
  currentRecipeItems.value.forEach(item => {
    if (item.foodId && !item.foodName) {
      const food = availableFoods.value.find(f => f.id === item.foodId);
      if (food) {
        item.foodName = food.name;
      }
    }
  });
};

// 确保热量正确计算
const ensureCalories = () => {
  currentRecipeItems.value.forEach(item => {
    if (item.foodId && item.quantity) {
      const food = availableFoods.value.find(f => f.id === item.foodId);
      if (food && food.caloriesPer100) {
        // 重新计算热量
        item.calories = (food.caloriesPer100 * item.quantity) / 100;
      }
    }
  });
};

const handleQuantityChange = (record: any, index: number) => {
  const food = availableFoods.value.find(f => f.id === record.foodId);
  if (food) {
    record.calories = (food.caloriesPer100 * record.quantity) / 100;
  }
};

const handleUnitChange = (record: any, index: number) => {
  // 单位变化时重新计算热量
  handleQuantityChange(record, index);
};

const handleSaveIngredients = async () => {
  try {
    if (!currentRecipe.value?.id) {
      message.error('食谱ID不存在');
      return;
    }

    // 过滤掉无效的食材项
    const validIngredients = currentRecipeItems.value.filter(item => 
      item.foodId && item.quantity && item.quantityUnit
    );

    if (validIngredients.length === 0) {
      message.warning('请至少添加一个有效食材');
      return;
    }

    // 准备保存的数据，确保包含必要的字段
    const ingredientsToSave = validIngredients.map(item => ({
      recipeId: currentRecipe.value!.id,
      foodId: item.foodId,
      quantity: item.quantity,
      quantityUnit: item.quantityUnit,
      calories: item.calories || 0
    }));

    // 调用保存食材的API
    try {
      await saveRecipeItems(currentRecipe.value.id, ingredientsToSave);
      message.success(`成功保存 ${validIngredients.length} 个食材`);
      itemsModalVisible.value = false;
      
      // 重新加载食材数据以确认保存
      await loadRecipeItems(currentRecipe.value.id);
    } catch (apiError) {
      // 如果API调用失败，尝试使用更新API
      try {
        await updateRecipeItems(currentRecipe.value.id, ingredientsToSave);
        message.success(`成功更新 ${validIngredients.length} 个食材`);
        itemsModalVisible.value = false;
        await loadRecipeItems(currentRecipe.value.id);
      } catch (updateError) {
        // 如果API都不可用，使用本地存储作为临时方案
        console.warn('API不可用，使用本地存储保存食材数据');
        localStorage.setItem(`recipe_${currentRecipe.value.id}_ingredients`, JSON.stringify(ingredientsToSave));
        message.success(`食材数据已保存到本地存储 (${validIngredients.length} 个食材)`);
        itemsModalVisible.value = false;
        
        // 更新本地数据
        currentRecipeItems.value = ingredientsToSave.map(item => ({
          ...item,
          foodName: availableFoods.value.find(f => f.id === item.foodId)?.name || '未知食材'
        }));
      }
    }
  } catch (error) {
    message.error('保存食材失败');
    console.error('保存食材错误:', error);
  }
};

// 加载食谱食材数据
const loadRecipeItems = async (recipeId: number) => {
  try {
    const result = await getRecipeItems(recipeId);
    currentRecipeItems.value = result.data;
    // 确保食材名称正确显示
    ensureFoodNames();
    // 确保热量正确计算
    ensureCalories();
  } catch (error) {
    // 如果API失败，尝试从本地存储加载
    try {
      const localData = localStorage.getItem(`recipe_${recipeId}_ingredients`);
      if (localData) {
        const ingredients = JSON.parse(localData);
        currentRecipeItems.value = ingredients.map((item: any) => ({
          ...item,
          foodName: availableFoods.value.find(f => f.id === item.foodId)?.name || '未知食材'
        }));
        console.log('从本地存储加载食材数据:', ingredients);
        // 确保热量正确计算
        ensureCalories();
      } else {
        currentRecipeItems.value = [];
      }
    } catch (localError) {
      console.error('从本地存储加载食材数据失败:', localError);
      message.error('加载食材数据失败');
    }
  }
};

const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss');
};

// 获取图片完整URL
const getImageUrl = (imageUrl: string) => {
  if (!imageUrl) return '';
  // 如果已经是完整URL，直接返回
  if (imageUrl.startsWith('http')) {
    return imageUrl;
  }
  // 如果是相对路径，使用API接口访问
  if (imageUrl.startsWith('/static/')) {
    // 从 /static/recipes/filename 提取 filename
    const filename = imageUrl.replace('/static/recipes/', '');
    return `http://localhost:8088/api/test/image/${filename}`;
  }
  return imageUrl;
};

// 初始化
onMounted(() => {
  loadRecipes();
  loadCategories();
  loadFoods();
});
</script>

<style scoped>
.recipes-page {
  padding: 0;
  background: #f5f5f5;
  min-height: 100vh;
}

/* 页面头部样式 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.header-left {
  flex: 1;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-icon {
  font-size: 32px;
}

.page-subtitle {
  font-size: 14px;
  margin: 0;
  opacity: 0.9;
}

.header-right {
  text-align: right;
}

/* 卡片样式 */
.search-card, .recipes-card {
  margin-bottom: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #1f2937;
}

.card-icon {
  font-size: 16px;
}

/* 食谱网格 */
.recipes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.recipe-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
  cursor: pointer;
}

.recipe-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  transform: translateY(-4px);
}

.recipe-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.recipe-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.recipe-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-icon {
  font-size: 48px;
  opacity: 0.5;
}

.recipe-status {
  position: absolute;
  top: 12px;
  right: 12px;
}

.status-tag {
  border-radius: 12px;
  font-weight: 500;
}

.recipe-content {
  padding: 20px;
}

.recipe-header {
  margin-bottom: 12px;
}

.recipe-name {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
  line-height: 1.4;
}

.recipe-category {
  font-size: 12px;
  color: #6b7280;
  background: #f3f4f6;
  padding: 4px 8px;
  border-radius: 8px;
}

.recipe-description {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.5;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.recipe-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #6b7280;
}

.meta-icon {
  font-size: 14px;
}

.meta-text {
  font-weight: 500;
}

.recipe-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

/* 加载和空状态 */
.loading-container, .empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
}

/* 弹窗样式 */
.recipe-modal :deep(.ant-modal-header),
.ingredients-modal :deep(.ant-modal-header),
.recipe-detail-modal :deep(.ant-modal-header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px 12px 0 0;
}

.modal-title {
  display: flex;
  align-items: center;
  gap: 8px;
  color: white;
  font-weight: 600;
  font-size: 16px;
}

.modal-icon {
  font-size: 18px;
}

/* 食材管理样式 */
.recipe-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.recipe-basic-info h3 {
  margin: 0 0 8px 0;
  color: #1f2937;
  font-size: 18px;
}

.recipe-desc {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

.ingredients-actions {
  display: flex;
  gap: 8px;
}

.ingredients-table {
  margin-top: 16px;
}

.calories-display {
  font-weight: 500;
  color: #f59e0b;
}

/* 食谱详情弹窗样式 */
.recipe-detail-content {
  max-height: 70vh;
  overflow-y: auto;
}

.recipe-basic-info {
  margin-bottom: 24px;
}

.recipe-header {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.recipe-image-section {
  flex-shrink: 0;
  width: 200px;
  height: 150px;
  border-radius: 12px;
  overflow: hidden;
}

.recipe-detail-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.recipe-detail-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.recipe-info-section {
  flex: 1;
}

.recipe-detail-name {
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 16px 0;
}

.recipe-meta-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.meta-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.meta-label {
  font-weight: 500;
  color: #6b7280;
  min-width: 80px;
}

.meta-value {
  color: #1f2937;
  font-weight: 500;
}

.recipe-description-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 12px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.recipe-description-text {
  color: #6b7280;
  line-height: 1.6;
  margin: 0;
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
  border-left: 4px solid #e5e7eb;
}

.ingredients-section {
  margin-bottom: 24px;
}

.ingredients-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 16px;
}

.ingredient-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.ingredient-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.ingredient-name {
  font-weight: 500;
  color: #1f2937;
}

.ingredient-quantity {
  font-size: 14px;
  color: #6b7280;
}

.ingredient-calories {
  font-weight: 500;
  color: #f59e0b;
  background: #fef3c7;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 14px;
}

.total-calories {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  padding: 16px;
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border-radius: 12px;
  border: 1px solid #f59e0b;
}

.total-label {
  font-weight: 500;
  color: #92400e;
}

.total-value {
  font-weight: 600;
  font-size: 18px;
  color: #92400e;
}

.empty-ingredients {
  text-align: center;
  padding: 40px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .recipe-header {
    flex-direction: column;
    gap: 16px;
  }

  .recipe-image-section {
    width: 100%;
    height: 200px;
  }

  .recipe-meta-info {
    gap: 6px;
  }

  .meta-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .meta-label {
    min-width: auto;
  }
}

/* 刷新按钮 */
.refresh-btn {
  border-radius: 8px;
  transition: all 0.2s;
}

.refresh-btn:hover {
  background-color: #f0f0f0;
  transform: rotate(180deg);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }

  .header-right {
    text-align: center;
  }

  .page-title {
    font-size: 24px;
  }

  .recipes-grid {
    grid-template-columns: 1fr;
  }

  .recipe-meta {
    flex-direction: column;
    gap: 8px;
  }

  .recipe-actions {
    flex-wrap: wrap;
  }
}
</style>


