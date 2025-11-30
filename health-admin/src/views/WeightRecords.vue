<template>
  <div class="weight-records-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">
          <span class="title-icon">⚖️</span>
          体重记录管理
        </h1>
        <p class="page-subtitle">管理和查看用户的体重记录信息</p>
      </div>
      <div class="header-right">
        <a-statistic title="总记录数" :value="pagination.total" />
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

    <!-- 用户卡片列表 -->
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
            :loading="usersLoading"
            class="refresh-btn"
          >
            <template #icon>
              <span>🔄</span>
            </template>
          </a-button>
        </a-tooltip>
      </template>
      
      <div v-if="usersLoading" class="loading-container">
        <a-spin size="large" />
      </div>
      
      <div v-else-if="users.length === 0" class="empty-container">
        <a-empty description="暂无用户数据" />
      </div>
      
      <div v-else class="users-grid">
        <UserCard
          v-for="user in users"
          :key="user.id"
          :user="user"
          @click="handleUserClick"
          @view-details="handleViewUserDetails"
        />
      </div>
    </a-card>

    <!-- 用户体重记录弹窗 -->
    <a-modal
      v-model:open="userRecordsVisible"
      :title="`${selectedUser?.nickname || '用户'}的体重记录`"
      :width="1200"
      :mask-closable="false"
      :destroy-on-close="true"
      class="user-records-modal"
    >
      <template #title>
        <div class="modal-title">
          <span class="modal-icon">⚖️</span>
          {{ selectedUser?.nickname || '用户' }}的体重记录
        </div>
      </template>

      <div v-if="userRecordsLoading" class="loading-container">
        <a-spin size="large" />
      </div>
      
      <div v-else-if="userRecords.length === 0" class="empty-container">
        <a-empty description="该用户暂无体重记录" />
      </div>
      
      <div v-else class="records-grid">
        <div
          v-for="record in userRecords"
          :key="record.id"
          class="record-card"
          @click="handleViewRecord(record)"
        >
          <div class="record-header">
            <div class="weight-info">
              <span class="weight-icon">⚖️</span>
              <span class="weight-value">{{ record.weight }}kg</span>
            </div>
            <div class="record-time">
              {{ formatDateTime(record.recordedAt) }}
            </div>
          </div>
          
          <div class="record-content">
            <div class="record-stats">
              <div class="stat-item">
                <span class="stat-label">BMI</span>
                <span class="stat-value" :class="getBmiClass(record.bmi)">
                  {{ record.bmi?.toFixed(1) || '-' }}
                </span>
              </div>
              <div class="stat-item">
                <span class="stat-label">状态</span>
                <span class="stat-value" :class="getBmiClass(record.bmi)">
                  {{ getBmiStatus(record.bmi) }}
                </span>
              </div>
            </div>
            
            <div v-if="record.note" class="record-note">
              {{ record.note }}
            </div>
          </div>
          
          <div class="record-actions">
            <a-button type="text" size="small" @click.stop="handleViewRecord(record)">
              <template #icon>
                <span>👁️</span>
              </template>
              查看详情
            </a-button>
            <a-button type="text" size="small" @click.stop="handleEditRecord(record)">
              <template #icon>
                <span>✏️</span>
              </template>
              编辑
            </a-button>
            <a-popconfirm
              title="确定要删除这条体重记录吗？"
              @confirm="handleDeleteRecord(record.id)"
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
    </a-modal>

    <!-- 体重记录详情弹窗 -->
    <a-modal
        v-model:open="detailVisible"
        :title="modalTitle"
        :width="800"
        :mask-closable="false"
        @ok="handleModalOk"
        @cancel="detailVisible = false"
    >
      <template #title>
        <div class="modal-title">
          <span class="modal-icon">⚖️</span>
          {{ modalTitle }}
        </div>
      </template>

      <a-form
          ref="formRef"
          :model="formData"
          :label-col="{ span: 6 }"
          :wrapper-col="{ span: 18 }"
      >
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="用户" name="userId">
              <a-input :value="formData.user?.nickname || ''" disabled />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="记录时间" name="recordedAt" :rules="[{ required: true, message: '请选择记录时间' }]">
              <a-date-picker
                  v-model:value="formData.recordedAt"
                  show-time
                  style="width: 100%"
                  :disabled="modalTitle === '查看体重记录'"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="体重" name="weight" :rules="[{ required: true, message: '请输入体重' }]">
              <a-input-number
                  v-model:value="formData.weight"
                  :min="20"
                  :max="300"
                  :precision="1"
                  style="width: 100%"
                  :disabled="modalTitle === '查看体重记录'"
                  addon-after="kg"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="BMI" name="bmi">
              <a-input
                  :value="formData.bmi?.toFixed(1) || '-'"
                  disabled
                  addon-after="kg/m²"
              />
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="备注" name="note">
              <a-textarea
                  v-model:value="formData.note"
                  :rows="3"
                  :disabled="modalTitle === '查看体重记录'"
              />
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
import dayjs from 'dayjs'
import UserCard from '@/components/UserCard.vue'
import { getWeightRecordList, getWeightRecordDetail, updateWeightRecord, deleteWeightRecord, type WeightRecord } from '@/api/weightRecord'
import { getUserList, type User } from '@/api/user'

// 响应式数据
const loading = ref(false)
const usersLoading = ref(false)
const userRecordsLoading = ref(false)
const users = ref<User[]>([])
const userRecords = ref<WeightRecord[]>([])
const selectedUser = ref<User | null>(null)
const userRecordsVisible = ref(false)
const detailVisible = ref(false)
const modalTitle = ref('')
const formRef = ref()

// 搜索表单
const searchForm = reactive({
  keyword: '',
  userId: undefined,
  startDate: undefined,
  endDate: undefined,
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
const formData = reactive<Partial<WeightRecord>>({})

// 获取BMI样式类
const getBmiClass = (bmi: number) => {
  if (!bmi) return ''
  if (bmi < 18.5) return 'bmi-underweight'
  if (bmi < 24) return 'bmi-normal'
  if (bmi < 28) return 'bmi-overweight'
  return 'bmi-obese'
}

// 获取BMI状态文本
const getBmiStatus = (bmi: number) => {
  if (!bmi) return '未知'
  if (bmi < 18.5) return '偏瘦'
  if (bmi < 24) return '正常'
  if (bmi < 28) return '超重'
  return '肥胖'
}

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss')
}

// 加载用户列表
const loadUsers = async () => {
  usersLoading.value = true
  try {
    const result = await getUserList({
      page: 1,
      size: 100, // 加载更多用户
    })
    users.value = result.data.records
  } catch (error) {
    message.error('加载用户列表失败')
  } finally {
    usersLoading.value = false
  }
}

// 加载用户体重记录
const loadUserRecords = async (userId: number) => {
  userRecordsLoading.value = true
  try {
    const result = await getWeightRecordList({
      userId,
      page: 1,
      size: 50,
    })
    userRecords.value = result.data.records
  } catch (error) {
    message.error('加载用户体重记录失败')
  } finally {
    userRecordsLoading.value = false
  }
}

// 搜索
const handleSearch = () => {
  loadUsers()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    userId: undefined,
    startDate: undefined,
    endDate: undefined,
  })
  loadUsers()
}

// 用户点击事件
const handleUserClick = async (user: User) => {
  selectedUser.value = user
  await loadUserRecords(user.id!)
  userRecordsVisible.value = true
}

// 查看用户详情
const handleViewUserDetails = (user: User) => {
  // 可以跳转到用户详情页面或显示用户详情弹窗
  message.info(`查看用户 ${user.nickname} 的详细信息`)
}

// 查看体重记录
const handleViewRecord = async (record: WeightRecord) => {
  modalTitle.value = '查看体重记录'
  try {
    const result = await getWeightRecordDetail(record.id!)
    Object.assign(formData, result.data, {
      recordedAt: dayjs(result.data.recordedAt)
    });
    detailVisible.value = true
  } catch (error) {
    message.error('获取详情失败')
  }
}

// 编辑体重记录
const handleEditRecord = async (record: WeightRecord) => {
  modalTitle.value = '编辑体重记录'
  try {
    const result = await getWeightRecordDetail(record.id!)
    Object.assign(formData, result.data, {
      recordedAt: dayjs(result.data.recordedAt)
    });
    detailVisible.value = true
  } catch (error) {
    message.error('获取详情失败')
  }
}

// 删除体重记录
const handleDeleteRecord = async (id: number) => {
  try {
    await deleteWeightRecord(id)
    message.success('删除成功')
    if (selectedUser.value) {
      await loadUserRecords(selectedUser.value.id!)
    }
  } catch (error) {
    message.error('删除失败')
  }
}

// 弹窗确定
const handleModalOk = async () => {
  if (modalTitle.value === '查看体重记录') {
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

    await updateWeightRecord(formData.id!, payload)
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

// 初始化
onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.weight-records-page {
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
.search-card, .users-card {
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

/* 用户网格 */
.users-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 16px;
}

/* 加载和空状态 */
.loading-container, .empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
}

/* 记录网格 */
.records-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
  max-height: 500px;
  overflow-y: auto;
}

.record-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
  cursor: pointer;
}

.record-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
  border-color: #1890ff;
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.weight-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.weight-icon {
  font-size: 20px;
}

.weight-value {
  font-weight: 600;
  color: #1f2937;
  font-size: 18px;
}

.record-time {
  font-size: 12px;
  color: #6b7280;
}

.record-content {
  margin-bottom: 12px;
}

.record-stats {
  display: flex;
  gap: 16px;
  margin-bottom: 8px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.stat-label {
  font-size: 11px;
  color: #9ca3af;
  margin-bottom: 2px;
}

.stat-value {
  font-size: 13px;
  font-weight: 500;
  color: #374151;
}

.record-note {
  font-size: 12px;
  color: #6b7280;
  background: #f9fafb;
  padding: 8px;
  border-radius: 6px;
  border-left: 3px solid #e5e7eb;
}

.record-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

/* 弹窗样式 */
.user-records-modal :deep(.ant-modal-header) {
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

/* 刷新按钮 */
.refresh-btn {
  border-radius: 8px;
  transition: all 0.2s;
}

.refresh-btn:hover {
  background-color: #f0f0f0;
  transform: rotate(180deg);
}

/* BMI样式 */
.bmi-underweight {
  color: #1890ff;
  font-weight: 600;
}

.bmi-normal {
  color: #52c41a;
  font-weight: 600;
}

.bmi-overweight {
  color: #faad14;
  font-weight: 600;
}

.bmi-obese {
  color: #ff4d4f;
  font-weight: 600;
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

  .users-grid {
    grid-template-columns: 1fr;
  }

  .records-grid {
    grid-template-columns: 1fr;
  }
}
</style>

