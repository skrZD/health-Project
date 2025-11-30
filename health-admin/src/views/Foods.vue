<template>
  <div class="foods-page">
    <div class="page-header">
      <h1 class="page-title">食物管理</h1>
      <a-button type="primary" @click="handleAdd">
        <template #icon>
          <PlusOutlined />
        </template>
        新增食物
      </a-button>
    </div>

    <!-- 搜索表单 -->
    <a-card class="search-card" :bordered="false">
      <a-form
        :model="searchForm"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 18 }"
        @finish="handleSearch"
      >
        <a-row :gutter="16">
          <a-col :span="6">
            <a-form-item label="关键词" name="keyword">
              <a-input v-model:value="searchForm.keyword" placeholder="请输入食物名称" />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="分类" name="categoryId">
              <a-select v-model:value="searchForm.categoryId" placeholder="请选择分类" allow-clear>
                <a-select-option v-for="category in categories" :key="category.id" :value="category.id">
                  {{ category.name }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="状态" name="enabled">
              <a-select v-model:value="searchForm.enabled" placeholder="请选择状态" allow-clear>
                <a-select-option :value="1">启用</a-select-option>
                <a-select-option :value="0">禁用</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item>
              <a-space>
                <a-button type="primary" html-type="submit">搜索</a-button>
                <a-button @click="handleReset">重置</a-button>
              </a-space>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-card>

    <!-- 数据表格 -->
    <a-card class="table-card" :bordered="false">
      <a-table
        :columns="columns"
        :data-source="tableData"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'enabled'">
            <a-tag :color="record.enabled ? 'green' : 'red'">
              {{ record.enabled ? '启用' : '禁用' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'category'">
            {{ getCategoryName(record.categoryId) }}
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="handleView(record)">
                查看
              </a-button>
              <a-button type="link" size="small" @click="handleEdit(record)">
                编辑
              </a-button>
              <a-popconfirm
                title="确定要删除这个食物吗？"
                @confirm="handleDelete(record.id)"
              >
                <a-button type="link" size="small" danger>
                  删除
                </a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 食物详情弹窗 -->
    <a-modal
      v-model:open="detailVisible"
      :title="modalTitle"
      :width="800"
      @ok="handleModalOk"
    >
      <a-form
        ref="formRef"
        :model="formData"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 18 }"
      >
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="食物名称" name="name" :rules="[{ required: true, message: '请输入食物名称' }]">
              <a-input v-model:value="formData.name" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="别名" name="alias">
              <a-input v-model:value="formData.alias" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="分类" name="categoryId" :rules="[{ required: true, message: '请选择分类' }]">
              <a-select v-model:value="formData.categoryId" placeholder="请选择分类">
                <a-select-option v-for="category in categories" :key="category.id" :value="category.id">
                  {{ category.name }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="单位" name="unit" :rules="[{ required: true, message: '请输入单位' }]">
              <a-input v-model:value="formData.unit" placeholder="如：g、ml、个" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="每100g热量" name="caloriesPer100" :rules="[{ required: true, message: '请输入热量' }]">
              <a-input-number v-model:value="formData.caloriesPer100" :min="0" :max="1000" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="蛋白质(g)" name="proteinGPer100">
              <a-input-number v-model:value="formData.proteinGPer100" :min="0" :max="100" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="脂肪(g)" name="fatGPer100">
              <a-input-number v-model:value="formData.fatGPer100" :min="0" :max="100" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="碳水化合物(g)" name="carbsGPer100">
              <a-input-number v-model:value="formData.carbsGPer100" :min="0" :max="100" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="钠(mg)" name="sodiumMgPer100">
              <a-input-number v-model:value="formData.sodiumMgPer100" :min="0" :max="10000" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="状态" name="enabled">
              <a-radio-group v-model:value="formData.enabled">
                <a-radio :value="1">启用</a-radio>
                <a-radio :value="0">禁用</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import { getFoodList, createFood, updateFood, deleteFood, getFoodCategories, type Food, type FoodCategory } from '@/api/food'

// 响应式数据
const loading = ref(false)
const tableData = ref<Food[]>([])
const categories = ref<FoodCategory[]>([])
const detailVisible = ref(false)
const modalTitle = ref('')
const formRef = ref()

// 搜索表单
const searchForm = reactive({
  keyword: '',
  categoryId: undefined,
  enabled: undefined,
  page: 1,
  size: 10,
})

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`,
})

// 表单数据
const formData = reactive<Partial<Food>>({
  enabled: 1,
})

// 表格列配置
const columns = [
  { title: '食物名称', dataIndex: 'name', key: 'name' },
  { title: '别名', dataIndex: 'alias', key: 'alias' },
  { title: '分类', dataIndex: 'categoryId', key: 'category' },
  { title: '单位', dataIndex: 'unit', key: 'unit' },
  { title: '每100g热量', dataIndex: 'caloriesPer100', key: 'caloriesPer100' },
  { title: '蛋白质(g)', dataIndex: 'proteinGPer100', key: 'proteinGPer100' },
  { title: '脂肪(g)', dataIndex: 'fatGPer100', key: 'fatGPer100' },
  { title: '状态', dataIndex: 'enabled', key: 'enabled' },
  { title: '操作', key: 'action', width: 200, fixed: 'right' },
]

// 获取分类名称
const getCategoryName = (categoryId: number) => {
  const category = categories.value.find(c => c.id === categoryId)
  return category ? category.name : '未知'
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      page: pagination.current,
      size: pagination.pageSize,
    }
    
    const result = await getFoodList(params)
    tableData.value = result.data.records
    pagination.total = result.data.total
  } catch (error) {
    message.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 加载分类数据
const loadCategories = async () => {
  try {
    const result = await getFoodCategories()
    categories.value = result.data
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    categoryId: undefined,
    enabled: undefined,
  })
  handleSearch()
}

// 表格变化
const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadData()
}

// 新增
const handleAdd = () => {
  modalTitle.value = '新增食物'
  Object.assign(formData, {
    enabled: 1,
  })
  detailVisible.value = true
}

// 查看
const handleView = (record: Food) => {
  modalTitle.value = '查看食物'
  Object.assign(formData, record)
  detailVisible.value = true
}

// 编辑
const handleEdit = (record: Food) => {
  modalTitle.value = '编辑食物'
  Object.assign(formData, record)
  detailVisible.value = true
}

// 删除
const handleDelete = async (id: number) => {
  try {
    await deleteFood(id)
    message.success('删除成功')
    loadData()
  } catch (error) {
    message.error('删除失败')
  }
}

// 弹窗确定
const handleModalOk = async () => {
  try {
    if (formData.id) {
      await updateFood(formData.id, formData)
      message.success('更新成功')
    } else {
      await createFood(formData)
      message.success('创建成功')
    }
    detailVisible.value = false
    loadData()
  } catch (error) {
    message.error('操作失败')
  }
}

// 初始化
onMounted(() => {
  loadCategories()
  loadData()
})
</script>

<style scoped>
.foods-page {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: #1f2937;
}

.search-card {
  margin-bottom: 24px;
}

.table-card {
  margin-bottom: 24px;
}
</style>
