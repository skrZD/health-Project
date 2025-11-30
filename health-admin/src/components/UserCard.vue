<template>
  <div class="user-card" @click="handleClick">
    <div class="user-avatar">
      <a-avatar :size="48" :src="user.avatar" :style="{ backgroundColor: avatarColor }">
        {{ user.nickname?.charAt(0) || 'U' }}
      </a-avatar>
    </div>
    <div class="user-info">
      <div class="user-name">{{ user.nickname || '未知用户' }}</div>
      <div class="user-details">
        <span v-if="user.gender" class="user-gender">{{ user.gender }}</span>
        <span v-if="user.age" class="user-age">{{ user.age }}岁</span>
        <span v-if="user.height" class="user-height">{{ user.height }}cm</span>
        <span v-if="user.weight" class="user-weight">{{ user.weight }}kg</span>
      </div>
      <div class="user-stats">
        <div class="stat-item" v-if="user.dailyCalorieGoal">
          <span class="stat-label">目标热量</span>
          <span class="stat-value">{{ user.dailyCalorieGoal }}千卡</span>
        </div>
        <div class="stat-item" v-if="user.dailyExerciseGoal">
          <span class="stat-label">运动目标</span>
          <span class="stat-value">{{ user.dailyExerciseGoal }}分钟</span>
        </div>
      </div>
    </div>
    <div class="user-actions">
      <a-button type="text" size="small" @click.stop="handleViewDetails">
        <template #icon>
          <EyeOutlined />
        </template>
        查看详情
      </a-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { EyeOutlined } from '@ant-design/icons-vue'
import type { User } from '@/api/user'

interface Props {
  user: User
  clickable?: boolean
}

interface Emits {
  (e: 'click', user: User): void
  (e: 'view-details', user: User): void
}

const props = withDefaults(defineProps<Props>(), {
  clickable: true
})

const emit = defineEmits<Emits>()

// 根据用户昵称生成头像颜色
const avatarColor = computed(() => {
  const colors = ['#1890ff', '#52c41a', '#faad14', '#f5222d', '#722ed1', '#13c2c2', '#eb2f96', '#fa8c16']
  const index = (props.user.nickname?.charCodeAt(0) || 0) % colors.length
  return colors[index]
})

const handleClick = () => {
  if (props.clickable) {
    emit('click', props.user)
  }
}

const handleViewDetails = () => {
  emit('view-details', props.user)
}
</script>

<style scoped>
.user-card {
  display: flex;
  align-items: center;
  padding: 16px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
  cursor: pointer;
  margin-bottom: 16px;
}

.user-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
  border-color: #1890ff;
}

.user-avatar {
  margin-right: 16px;
  flex-shrink: 0;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-details {
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.user-details span {
  font-size: 12px;
  color: #6b7280;
  background: #f3f4f6;
  padding: 2px 8px;
  border-radius: 12px;
}

.user-stats {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
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

.user-actions {
  flex-shrink: 0;
  margin-left: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-card {
    flex-direction: column;
    text-align: center;
    padding: 20px;
  }
  
  .user-avatar {
    margin-right: 0;
    margin-bottom: 12px;
  }
  
  .user-actions {
    margin-left: 0;
    margin-top: 12px;
  }
  
  .user-details {
    justify-content: center;
  }
  
  .user-stats {
    justify-content: center;
  }
}
</style>

