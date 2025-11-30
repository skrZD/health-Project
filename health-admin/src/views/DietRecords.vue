<template>
  <div class="diet-records-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">
          <span class="title-icon">🍽️</span>
          饮食记录管理
        </h1>
        <p class="page-subtitle">管理和查看用户的饮食记录信息</p>
      </div>
      <div class="header-right">
        <a-statistic title="总记录数" :value="pagination.total" />
      </div>
    </div>

    <!-- 搜索卡片 -->
    <a-card class="search-card" :bordered="false">
      <template #title>
        <div class="card-title">
          <span class="card-icon">🔍</span>
          搜索筛选
        </div>
      </template>
      <a-form
          :model="searchForm"
          :label-col="{ span: 6 }"
          :wrapper-col="{ span: 18 }"
          @finish="handleSearch"
      >
        <a-row :gutter="[16, 16]">
          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <a-form-item label="关键词" name="keyword">
              <a-input
                v-model:value="searchForm.keyword"
                placeholder="请输入备注内容"
                allow-clear
                prefix="📝"
              />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <a-form-item label="用户ID" name="userId">
              <a-input-number
                v-model:value="searchForm.userId"
                placeholder="请输入用户ID"
                style="width: 100%"
                :min="1"
                prefix="👤"
              />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <a-form-item label="餐别" name="mealType">
              <a-select
                v-model:value="searchForm.mealType"
                placeholder="请选择餐别"
                allow-clear
                style="width: 100%"
              >
                <a-select-option value="早餐">
                  <span class="meal-option">🌅 早餐</span>
                </a-select-option>
                <a-select-option value="午餐">
                  <span class="meal-option">☀️ 午餐</span>
                </a-select-option>
                <a-select-option value="晚饭">
                  <span class="meal-option">🌙 晚饭</span>
                </a-select-option>
                <a-select-option value="加餐">
                  <span class="meal-option">🍎 加餐</span>
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <a-form-item>
              <a-space>
                <a-button type="primary" html-type="submit" :loading="loading">
                  <template #icon>
                    <span>🔍</span>
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
        <a-row :gutter="[16, 16]">
          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <a-form-item label="开始日期" name="startDate">
              <a-date-picker
                v-model:value="searchForm.startDate"
                style="width: 100%"
                placeholder="选择开始日期"
                format="YYYY-MM-DD"
              />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <a-form-item label="结束日期" name="endDate">
              <a-date-picker
                v-model:value="searchForm.endDate"
                style="width: 100%"
                placeholder="选择结束日期"
                format="YYYY-MM-DD"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-card>

    <!-- 用户卡片网格 -->
    <a-card class="users-card" :bordered="false">
      <template #title>
        <div class="card-title">
          <span class="card-icon">👥</span>
          用户列表
        </div>
      </template>
      <template #extra>
        <a-tooltip title="刷新数据">
          <a-button
            type="text"
            @click="loadUsers"
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
      
      <div v-else-if="users.length === 0" class="empty-container">
        <a-empty description="暂无用户数据" />
      </div>
      
      <div v-else class="users-grid">
        <div
          v-for="user in users"
          :key="user.id"
          class="user-card"
          @click="handleViewUserRecords(user)"
        >
          <div class="user-avatar">
            <a-avatar :size="60" :style="{ backgroundColor: '#1890ff' }">
              {{ user.nickname?.charAt(0) || 'U' }}
            </a-avatar>
          </div>
          
          <div class="user-content">
            <div class="user-header">
              <h3 class="user-name">{{ user.nickname || '未知用户' }}</h3>
              <span class="user-id">ID: {{ user.id }}</span>
            </div>
            
            <div class="user-info">
              <div class="info-grid">
                <div class="info-card">
                  <div class="info-icon">👤</div>
                  <div class="info-content">
                    <div class="info-label">性别</div>
                    <div class="info-value">{{ user.gender || '未知' }}</div>
                  </div>
                </div>
                <div class="info-card">
                  <div class="info-icon">🎂</div>
                  <div class="info-content">
                    <div class="info-label">年龄</div>
                    <div class="info-value">{{ user.age || '未知' }}岁</div>
                  </div>
                </div>
                <div class="info-card">
                  <div class="info-icon">📏</div>
                  <div class="info-content">
                    <div class="info-label">身高</div>
                    <div class="info-value">{{ user.height || '未知' }}cm</div>
                  </div>
                </div>
                <div class="info-card">
                  <div class="info-icon">⚖️</div>
                  <div class="info-content">
                    <div class="info-label">体重</div>
                    <div class="info-value">{{ user.weight || '未知' }}kg</div>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="user-goals">
              <div class="goal-item">
                <span class="goal-icon">🔥</span>
                <span class="goal-text">热量目标: {{ user.dailyCalorieGoal || 0 }}千卡</span>
              </div>
              <div class="goal-item">
                <span class="goal-icon">🏃</span>
                <span class="goal-text">运动目标: {{ user.dailyExerciseGoal || 0 }}分钟</span>
              </div>
            </div>
          </div>
          
          <div class="user-actions">
            <a-button type="primary" size="small" @click.stop="handleViewUserRecords(user)">
              <template #icon>
                <span>👁️</span>
              </template>
              查看记录
            </a-button>
          </div>
        </div>
      </div>
    </a-card>

    <!-- 用户饮食记录弹窗 -->
    <a-modal
        v-model:open="userRecordsVisible"
        :title="`${currentUser?.nickname || '用户'}的饮食记录`"
        width="1200px"
        class="user-records-modal"
        :footer="null"
    >
      <template #title>
        <div class="modal-title">
          <span class="modal-icon">🍽️</span>
          {{ currentUser?.nickname || '用户' }}的饮食记录
        </div>
      </template>

      <div v-if="currentUser" class="user-records-content">
        <!-- 用户基本信息 -->
        <div class="user-basic-info">
          <div class="user-header">
            <div class="user-avatar-section">
              <a-avatar :size="80" :style="{ backgroundColor: '#1890ff' }">
                {{ currentUser.nickname?.charAt(0) || 'U' }}
              </a-avatar>
            </div>
            
            <div class="user-info-section">
              <h2 class="user-detail-name">{{ currentUser.nickname || '未知用户' }}</h2>
              <div class="user-meta-info">
                <div class="meta-row">
                  <span class="meta-label">用户ID：</span>
                  <span class="meta-value">{{ currentUser.id }}</span>
                </div>
                <div class="meta-row">
                  <span class="meta-label">性别：</span>
                  <span class="meta-value">{{ currentUser.gender || '未知' }}</span>
                </div>
                <div class="meta-row">
                  <span class="meta-label">年龄：</span>
                  <span class="meta-value">{{ currentUser.age || '未知' }}岁</span>
                </div>
                <div class="meta-row">
                  <span class="meta-label">身高：</span>
                  <span class="meta-value">{{ currentUser.height || '未知' }}cm</span>
                </div>
                <div class="meta-row">
                  <span class="meta-label">体重：</span>
                  <span class="meta-value">{{ currentUser.weight || '未知' }}kg</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 饮食记录列表 -->
        <div class="records-section">
          <h3 class="section-title">📊 饮食记录</h3>
          
          <div v-if="userRecords.length === 0" class="empty-records">
            <a-empty description="该用户暂无饮食记录" />
          </div>
          
          <div v-else class="records-grid">
            <div
              v-for="record in userRecords"
              :key="record.id"
              class="record-card"
              @click="handleViewRecord(record)"
            >
              <div class="record-header">
                <div class="meal-type">
                  <a-tag :color="getMealTypeColor(record.mealType)" class="meal-tag">
                    <span class="meal-icon">{{ getMealIcon(record.mealType) }}</span>
                    {{ record.mealType }}
                  </a-tag>
                </div>
                <div class="record-time">
                  <div class="date">{{ formatDate(record.recordedAt) }}</div>
                  <div class="time">{{ formatTime(record.recordedAt) }}</div>
                </div>
              </div>
              
              <div class="record-content">
                <div class="calories-info">
                  <span class="calories-label">计划热量：</span>
                  <span class="calories-value">{{ record.planCalories || 0 }}千卡</span>
                </div>
                
                <div v-if="record.note" class="record-note">
                  <span class="note-label">备注：</span>
                  <span class="note-text">{{ record.note }}</span>
                </div>
                
                <div v-if="record.items && record.items.length > 0" class="record-items">
                  <div class="items-label">食物明细：</div>
                  <div class="items-list">
                    <div
                      v-for="item in record.items.slice(0, 3)"
                      :key="item.id"
                      class="item-tag"
                    >
                      {{ item.foodName || item.recipeName }} ({{ item.quantity }}{{ item.quantityUnit }})
                    </div>
                    <div v-if="record.items.length > 3" class="more-items">
                      +{{ record.items.length - 3 }} 更多...
                    </div>
                  </div>
                </div>
              </div>
              
              <div class="record-actions">
                <a-button type="text" size="small" @click.stop="handleViewRecord(record)">
                  <template #icon>
                    <span>👁️</span>
                  </template>
                  查看
                </a-button>
                <a-button type="text" size="small" danger @click.stop="handleDeleteRecord(record.id)">
                  <template #icon>
                    <span>🗑️</span>
                  </template>
                  删除
                </a-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </a-modal>

    <!-- 饮食记录详情弹窗 -->
    <a-modal
        v-model:open="detailVisible"
        :title="modalTitle"
        :width="1200"
        :mask-closable="false"
        :destroy-on-close="true"
        @ok="handleModalOk"
        @cancel="detailVisible = false"
        class="diet-modal"
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
          :label-col="{ span: 6 }"
          :wrapper-col="{ span: 18 }"
          class="diet-form"
      >
        <!-- 基本信息 -->
        <a-card size="small" class="form-section">
          <template #title>
            <span class="section-title">📋 基本信息</span>
          </template>
          <a-row :gutter="[16, 16]">
            <a-col :span="12">
              <a-form-item label="用户" name="userId">
                <a-input
                  :value="formData.user?.nickname || ''"
                  disabled
                  prefix="👤"
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="餐别" name="mealType" :rules="[{ required: true, message: '请选择餐别' }]">
                <a-select
                  v-model:value="formData.mealType"
                  :disabled="modalTitle === '查看饮食记录'"
                  placeholder="请选择餐别"
                >
                  <a-select-option value="早餐">
                    <span class="meal-option">🌅 早餐</span>
                  </a-select-option>
                  <a-select-option value="午餐">
                    <span class="meal-option">☀️ 午餐</span>
                  </a-select-option>
                  <a-select-option value="晚饭">
                    <span class="meal-option">🌙 晚饭</span>
                  </a-select-option>
                  <a-select-option value="加餐">
                    <span class="meal-option">🍎 加餐</span>
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="记录时间" name="recordedAt" :rules="[{ required: true, message: '请选择记录时间' }]">
                <a-date-picker
                    v-model:value="formData.recordedAt"
                    show-time
                    style="width: 100%"
                    :disabled="modalTitle === '查看饮食记录'"
                    placeholder="选择记录时间"
                    format="YYYY-MM-DD HH:mm:ss"
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="计划热量" name="planCalories">
                <a-input-number
                    v-model:value="formData.planCalories"
                    :min="0"
                    :max="5000"
                    style="width: 100%"
                    :disabled="modalTitle === '查看饮食记录'"
                    placeholder="请输入计划热量"
                    addon-after="千卡"
                />
              </a-form-item>
            </a-col>
            <a-col :span="24">
              <a-form-item label="备注" name="note">
                <a-textarea
                    v-model:value="formData.note"
                    :rows="3"
                    :disabled="modalTitle === '查看饮食记录'"
                    placeholder="请输入备注信息"
                    show-count
                    :maxlength="200"
                />
              </a-form-item>
            </a-col>
          </a-row>
        </a-card>

        <!-- 饮食详情 -->
        <a-card size="small" class="form-section">
          <template #title>
            <span class="section-title">🍽️ 饮食详情</span>
          </template>
          <a-table
              :columns="itemColumns"
              :data-source="formData.items || []"
              :pagination="false"
              size="small"
              class="items-table"
              :locale="{ emptyText: '暂无饮食记录' }"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'calories'">
                <div class="calories-cell">
                  <span class="calories-value">{{ record.calories || 0 }}</span>
                  <span class="calories-unit">千卡</span>
                </div>
              </template>
              <template v-else-if="column.key === 'quantity'">
                <div class="quantity-cell">
                  <span class="quantity-value">{{ record.quantity || 0 }}</span>
                  <span class="quantity-unit">{{ record.unit || 'g' }}</span>
                </div>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { getDietRecordList, getDietRecordDetail, updateDietRecord, deleteDietRecord, type DietRecord, type DietRecordItem } from '@/api/dietRecord'
import { getUserList, type User } from '@/api/user'

// 响应式数据
const loading = ref(false)
const tableData = ref<DietRecord[]>([])
const detailVisible = ref(false)
const modalTitle = ref('')
const formRef = ref()

// 新增：用户相关数据
const users = ref<User[]>([])
const currentUser = ref<User | null>(null)
const userRecords = ref<DietRecord[]>([])
const userRecordsVisible = ref(false)

// 搜索表单
const searchForm = reactive({
  keyword: '',
  userId: undefined,
  mealType: undefined,
  startDate: undefined,
  endDate: undefined,
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
const formData = reactive<Partial<DietRecord>>({
  items: [],
})

// 表格列配置
const columns = [
  { title: '用户', dataIndex: ['user', 'nickname'], key: 'user' },
  { title: '餐别', dataIndex: 'mealType', key: 'mealType' },
  { title: '记录时间', dataIndex: 'recordedAt', key: 'recordedAt' },
  { title: '计划热量', dataIndex: 'planCalories', key: 'planCalories' },
  { title: '备注', dataIndex: 'note', key: 'note', ellipsis: true },
  { title: '操作', key: 'action', width: 200, fixed: 'right' },
]

// 饮食记录项表格列配置
const itemColumns = [
  { title: '食物名称', dataIndex: 'foodName', key: 'foodName' },
  { title: '数量', dataIndex: 'quantity', key: 'quantity' },
  { title: '单位', dataIndex: 'unit', key: 'unit' },
  { title: '热量', dataIndex: 'calories', key: 'calories' },
]

// 获取餐别颜色
const getMealTypeColor = (mealType: string) => {
  switch (mealType) {
    case '早餐': return 'orange'
    case '午餐': return 'blue'
    case '晚饭': return 'purple'
    case '加餐': return 'green'
    default: return 'default'
  }
}

// 获取餐别图标
const getMealIcon = (mealType: string) => {
  switch (mealType) {
    case '早餐': return '🌅'
    case '午餐': return '☀️'
    case '晚饭': return '🌙'
    case '加餐': return '🍎'
    default: return '🍽️'
  }
}

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss')
}

// 格式化日期
const formatDate = (dateTime: string) => {
  return dayjs(dateTime).format('YYYY-MM-DD')
}

// 格式化时间
const formatTime = (dateTime: string) => {
  return dayjs(dateTime).format('HH:mm:ss')
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      page: pagination.current,
      size: pagination.pageSize,
      startDate: searchForm.startDate ? dayjs(searchForm.startDate).format('YYYY-MM-DD') : undefined,
      endDate: searchForm.endDate ? dayjs(searchForm.endDate).format('YYYY-MM-DD') : undefined,
    }
    const result = await getDietRecordList(params)
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
    userId: undefined,
    mealType: undefined,
    startDate: undefined,
    endDate: undefined,
  })
  handleSearch()
}

// 表格变化
const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadData()
}

// 查看
const handleView = async (record: DietRecord) => {
  modalTitle.value = '查看饮食记录'
  try {
    const result = await getDietRecordDetail(record.id!)
    Object.assign(formData, result.data, {
      recordedAt: dayjs(result.data.recordedAt) // 将时间字符串转换为 Day.js 对象
    });
    detailVisible.value = true
  } catch (error) {
    message.error('获取详情失败')
  }
}

// 编辑
const handleEdit = async (record: DietRecord) => {
  modalTitle.value = '编辑饮食记录'
  try {
    const result = await getDietRecordDetail(record.id!)
    Object.assign(formData, result.data, {
      recordedAt: dayjs(result.data.recordedAt)
    });
    detailVisible.value = true
  } catch (error) {
    message.error('获取详情失败')
  }
}

// 删除
const handleDelete = async (id: number) => {
  try {
    await deleteDietRecord(id)
    message.success('删除成功')
    loadData()
  } catch (error) {
    message.error('删除失败')
  }
}

// 弹窗确定
const handleModalOk = async () => {
  if (modalTitle.value === '查看饮食记录') {
    detailVisible.value = false
    return
  }

  try {
    // 提交前进行表单验证
    await formRef.value.validate()

    // 创建一个深拷贝，避免直接修改原始对象
    const payload = {
      ...formData,
      recordedAt: formData.recordedAt?.toISOString(), // 转换回 ISO 格式
    };

    await updateDietRecord(formData.id!, payload)
    message.success('更新成功')
    detailVisible.value = false
    loadData()
  } catch (error) {
    if (error.errorFields) {
      message.error('请填写完整信息')
    } else {
      message.error('操作失败')
    }
  }
}

// 新增：加载用户列表
const loadUsers = async () => {
  loading.value = true
  try {
    const result = await getUserList()
    users.value = result.data
  } catch (error) {
    message.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

// 新增：查看用户饮食记录
const handleViewUserRecords = async (user: User) => {
  currentUser.value = user
  try {
    const result = await getDietRecordList({
      userId: user.id,
      page: 1,
      size: 100
    })
    userRecords.value = result.data.records
    userRecordsVisible.value = true
  } catch (error) {
    message.error('加载用户饮食记录失败')
  }
}

// 新增：查看单个记录
const handleViewRecord = async (record: DietRecord) => {
  modalTitle.value = '查看饮食记录'
  try {
    const result = await getDietRecordDetail(record.id!)
    Object.assign(formData, result.data, {
      recordedAt: dayjs(result.data.recordedAt)
    });
    detailVisible.value = true
  } catch (error) {
    message.error('获取详情失败')
  }
}

// 新增：删除记录
const handleDeleteRecord = async (id: number) => {
  try {
    await deleteDietRecord(id)
    message.success('删除成功')
    // 重新加载用户记录
    if (currentUser.value) {
      await handleViewUserRecords(currentUser.value)
    }
  } catch (error) {
    message.error('删除失败')
  }
}

// 初始化
onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.diet-records-page {
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
.search-card, .table-card {
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

/* 表格样式 */
.diet-table {
  border-radius: 8px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-name {
  font-weight: 500;
  color: #1f2937;
}

.meal-tag {
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 500;
  border-radius: 16px;
  padding: 4px 12px;
}

.meal-icon {
  font-size: 12px;
}

.time-info {
  text-align: left;
}

.date {
  font-weight: 500;
  color: #1f2937;
  font-size: 14px;
}

.time {
  color: #6b7280;
  font-size: 12px;
}

.calories-info {
  text-align: center;
}

.calories-value {
  font-weight: 600;
  color: #f59e0b;
  font-size: 16px;
}

.calories-unit {
  color: #6b7280;
  font-size: 12px;
  margin-left: 4px;
}

.note-content {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #6b7280;
}

.action-btn {
  border-radius: 6px;
  transition: all 0.2s;
}

.action-btn:hover {
  transform: scale(1.1);
}

.view-btn:hover {
  background-color: #e6f7ff;
  color: #1890ff;
}

.edit-btn:hover {
  background-color: #f6ffed;
  color: #52c41a;
}

.delete-btn:hover {
  background-color: #fff2f0;
  color: #ff4d4f;
}

.refresh-btn {
  border-radius: 8px;
  transition: all 0.2s;
}

.refresh-btn:hover {
  background-color: #f0f0f0;
  transform: rotate(180deg);
}

/* 弹窗样式 */
.diet-modal :deep(.ant-modal-header) {
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

.diet-form {
  padding: 16px 0;
}

.form-section {
  margin-bottom: 16px;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
}

.form-section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-weight: 600;
  color: #1f2937;
  font-size: 14px;
}

.items-table {
  margin-top: 8px;
}

.calories-cell, .quantity-cell {
  text-align: center;
}

.calories-value, .quantity-value {
  font-weight: 600;
  color: #f59e0b;
  font-size: 14px;
}

.calories-unit, .quantity-unit {
  color: #6b7280;
  font-size: 12px;
  margin-left: 4px;
}

/* 餐别选项样式 */
.meal-option {
  display: flex;
  align-items: center;
  gap: 6px;
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

  .diet-table {
    font-size: 12px;
  }
}

/* 空状态样式 */
:deep(.ant-table-tbody > tr.ant-table-placeholder > td) {
  text-align: center;
  color: #999;
  font-style: italic;
}

/* 加载状态样式 */
:deep(.ant-spin-container) {
  min-height: 200px;
}

/* 分页样式 */
:deep(.ant-pagination) {
  text-align: right;
  margin-top: 16px;
}

/* 表单验证样式 */
:deep(.ant-form-item-explain-error) {
  font-size: 12px;
}

/* 按钮组样式 */
:deep(.ant-space) {
  width: 100%;
}

:deep(.ant-space-item) {
  flex: 1;
}

/* 卡片阴影效果 */
.search-card:hover, .table-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.3s ease;
}

/* 新增：用户卡片网格样式 */
.users-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  padding: 20px 0;
}

.user-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid #f0f0f0;
}

.user-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  border-color: #1890ff;
}

.user-avatar {
  text-align: center;
  margin-bottom: 16px;
}

.user-content {
  margin-bottom: 16px;
}

.user-header {
  text-align: center;
  margin-bottom: 16px;
}

.user-name {
  font-size: 18px;
  font-weight: 600;
  color: #262626;
  margin: 0 0 4px 0;
}

.user-id {
  font-size: 12px;
  color: #8c8c8c;
  background: #f5f5f5;
  padding: 2px 8px;
  border-radius: 4px;
}

.user-info {
  margin-bottom: 16px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  justify-items: center;
  width: 100%;
}

.info-card {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  border: 1px solid #e9ecef;
  width: 100%;
  max-width: 140px;
  min-width: 120px;
  text-align: center;
}

.info-card:hover {
  background: #e3f2fd;
  border-color: #1890ff;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.15);
}

.info-icon {
  font-size: 20px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  border-radius: 50%;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.info-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  width: 100%;
}

.info-label {
  font-size: 12px;
  color: #8c8c8c;
  font-weight: 500;
  line-height: 1.2;
  text-align: center;
  white-space: nowrap;
}

.info-value {
  font-size: 14px;
  color: #262626;
  font-weight: 600;
  line-height: 1.2;
  text-align: center;
  white-space: nowrap;
}

.user-goals {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 16px;
}

.goal-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #595959;
  margin-bottom: 4px;
}

.goal-item:last-child {
  margin-bottom: 0;
}

.goal-icon {
  font-size: 14px;
}

.goal-text {
  font-weight: 500;
}

.user-actions {
  text-align: center;
}

/* 用户记录弹窗样式 */
.user-records-modal .ant-modal-body {
  padding: 0;
}

.user-records-content {
  padding: 24px;
}

.user-basic-info {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
}

.user-header {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-avatar-section {
  flex-shrink: 0;
}

.user-info-section {
  flex: 1;
}

.user-detail-name {
  font-size: 24px;
  font-weight: 700;
  color: #262626;
  margin: 0 0 16px 0;
}

.user-meta-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.meta-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.meta-label {
  font-weight: 500;
  color: #8c8c8c;
  min-width: 60px;
}

.meta-value {
  font-weight: 600;
  color: #262626;
}

.records-section {
  margin-top: 24px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #262626;
  margin: 0 0 16px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.records-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.record-card {
  background: white;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.record-card:hover {
  border-color: #1890ff;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.15);
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.meal-tag {
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 500;
}

.meal-icon {
  font-size: 14px;
}

.record-time {
  text-align: right;
  font-size: 12px;
  color: #8c8c8c;
}

.date {
  font-weight: 500;
  color: #262626;
}

.time {
  margin-top: 2px;
}

.record-content {
  margin-bottom: 12px;
}

.calories-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.calories-label {
  font-size: 13px;
  color: #8c8c8c;
}

.calories-value {
  font-weight: 600;
  color: #f59e0b;
  background: #fef3c7;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 13px;
}

.record-note {
  margin-bottom: 8px;
}

.note-label {
  font-size: 13px;
  color: #8c8c8c;
  margin-right: 4px;
}

.note-text {
  font-size: 13px;
  color: #595959;
  font-style: italic;
}

.record-items {
  margin-top: 8px;
}

.items-label {
  font-size: 13px;
  color: #8c8c8c;
  margin-bottom: 6px;
  font-weight: 500;
}

.items-list {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.item-tag {
  background: #f0f0f0;
  color: #595959;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.more-items {
  background: #e6f7ff;
  color: #1890ff;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.record-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding-top: 8px;
  border-top: 1px solid #f0f0f0;
}

.empty-records {
  text-align: center;
  padding: 40px 0;
}

/* 加载和空状态样式 */
.loading-container, .empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .users-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .user-header {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }
  
  .user-meta-info {
    grid-template-columns: 1fr;
  }
  
  .records-grid {
    grid-template-columns: 1fr;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
    gap: 8px;
  }
  
  .info-card {
    padding: 10px;
  }
  
  .info-icon {
    width: 28px;
    height: 28px;
    font-size: 16px;
  }
  
  .info-label {
    font-size: 11px;
  }
  
  .info-value {
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .info-card {
    padding: 8px;
    gap: 8px;
  }
  
  .info-icon {
    width: 24px;
    height: 24px;
    font-size: 14px;
  }
}
</style>
