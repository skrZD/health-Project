<template>
  <div class="users-page">
    <div class="page-header">
      <h1 class="page-title">用户管理</h1>
      <a-button type="primary" @click="handleAdd">
        <template #icon>
          <PlusOutlined />
        </template>
        新增用户
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
              <a-input v-model:value="searchForm.keyword" placeholder="请输入昵称或openid" />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="性别" name="gender">
              <a-select v-model:value="searchForm.gender" placeholder="请选择性别" allow-clear>
                <a-select-option value="男">男</a-select-option>
                <a-select-option value="女">女</a-select-option>
                <a-select-option value="其他">其他</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="注册时间" name="dateRange">
              <a-range-picker v-model:value="searchForm.dateRange" />
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
          <template v-if="column.key === 'avatar'">
            <a-avatar :src="record.avatar" :size="32">
              {{ record.nickname?.charAt(0) }}
            </a-avatar>
          </template>
          <template v-else-if="column.key === 'gender'">
            <a-tag :color="getGenderColor(record.gender)">
              {{ record.gender || '未知' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'createdAt'">
            {{ formatDate(record.createdAt) }}
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
                title="确定要删除这个用户吗？"
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

    <!-- 用户详情弹窗 -->
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
            <a-form-item label="昵称" name="nickname">
              <a-input v-model:value="formData.nickname" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="姓名" name="name">
              <a-input v-model:value="formData.name" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="性别" name="gender">
              <a-select v-model:value="formData.gender">
                <a-select-option value="男">男</a-select-option>
                <a-select-option value="女">女</a-select-option>
                <a-select-option value="其他">其他</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="年龄" name="age">
              <a-input-number v-model:value="formData.age" :min="0" :max="150" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="身高(cm)" name="height">
              <a-input-number v-model:value="formData.height" :min="0" :max="300" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="体重(kg)" name="weight">
              <a-input-number v-model:value="formData.weight" :min="0" :max="500" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="OpenID" name="openid">
              <a-input v-model:value="formData.openid" disabled />
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
import { getUserList, deleteUser, updateUser, type User, type UserQueryParams } from '@/api/user'
import dayjs from 'dayjs'

// 响应式数据
const loading = ref(false)
const tableData = ref<User[]>([])
const detailVisible = ref(false)
const modalTitle = ref('')
const formRef = ref()

// 搜索表单
const searchForm = reactive<UserQueryParams>({
  keyword: '',
  gender: undefined,
  dateRange: undefined,
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
const formData = reactive<Partial<User>>({})

// 表格列配置
const columns = [
  {
    title: '头像',
    dataIndex: 'avatar',
    key: 'avatar',
    width: 80,
  },
  {
    title: '昵称',
    dataIndex: 'nickname',
    key: 'nickname',
  },
  {
    title: '姓名',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: '性别',
    dataIndex: 'gender',
    key: 'gender',
    width: 80,
  },
  {
    title: '年龄',
    dataIndex: 'age',
    key: 'age',
    width: 80,
  },
  {
    title: '身高(cm)',
    dataIndex: 'height',
    key: 'height',
    width: 100,
  },
  {
    title: '体重(kg)',
    dataIndex: 'weight',
    key: 'weight',
    width: 100,
  },
  {
    title: '注册时间',
    dataIndex: 'createdAt',
    key: 'createdAt',
    width: 180,
  },
  {
    title: '操作',
    key: 'action',
    width: 200,
    fixed: 'right',
  },
]

// 获取性别颜色
const getGenderColor = (gender?: string) => {
  switch (gender) {
    case '男': return 'blue'
    case '女': return 'pink'
    case '其他': return 'green'
    default: return 'default'
  }
}

// 格式化日期
const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
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
    
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startDate = searchForm.dateRange[0].format('YYYY-MM-DD')
      params.endDate = searchForm.dateRange[1].format('YYYY-MM-DD')
    }
    
    const result = await getUserList(params)
    tableData.value = result.data.records
    pagination.total = result.data.total
  } catch (error) {
    message.error('加载数据失败')
  } finally {
    loading.value = false
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
    gender: undefined,
    dateRange: undefined,
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
  modalTitle.value = '新增用户'
  Object.assign(formData, {})
  detailVisible.value = true
}

// 查看
const handleView = (record: User) => {
  modalTitle.value = '查看用户'
  Object.assign(formData, record)
  detailVisible.value = true
}

// 编辑
const handleEdit = (record: User) => {
  modalTitle.value = '编辑用户'
  Object.assign(formData, record)
  detailVisible.value = true
}

// 删除
const handleDelete = async (id: number) => {
  try {
    await deleteUser(id)
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
      await updateUser(formData.id, formData)
      message.success('更新成功')
    }
    detailVisible.value = false
    loadData()
  } catch (error) {
    message.error('操作失败')
  }
}

// 初始化
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.users-page {
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

