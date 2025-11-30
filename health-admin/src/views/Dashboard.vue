<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h1 class="page-title">
        <DashboardOutlined />
        仪表盘
      </h1>
      <div class="header-actions">
        <a-button type="primary" @click="refreshData" :loading="loading">
          <ReloadOutlined />
          刷新数据
        </a-button>
        <a-range-picker v-model:value="dateRange" @change="onDateRangeChange" />
      </div>
    </div>
    
    <!-- 统计卡片 -->
    <a-row :gutter="20" class="stats-row">
      <a-col :span="4">
        <a-card class="stat-card user-card" hoverable>
          <div class="stat-content">
            <div class="stat-icon">
              <UserOutlined />
            </div>
            <div class="stat-info">
          <a-statistic
            title="总用户数"
            :value="stats.totalUsers"
                :value-style="{ color: '#1890ff', fontSize: '28px', fontWeight: 'bold' }"
            suffix="人"
              />
              <div class="stat-trend">
                <span class="trend-text positive">+{{ stats.newUsersToday }}</span>
<!--                <span class="trend-label">今日新增</span>-->
              </div>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="4">
        <a-card class="stat-card diet-card" hoverable>
          <div class="stat-content">
            <div class="stat-icon">
              <CoffeeOutlined />
            </div>
            <div class="stat-info">
          <a-statistic
            title="饮食记录"
            :value="stats.totalDietRecords"
                :value-style="{ color: '#52c41a', fontSize: '28px', fontWeight: 'bold' }"
            suffix="条"
              />
              <div class="stat-trend">
                <span class="trend-text positive">+{{ stats.todayDietRecords }}</span>
<!--                <span class="trend-label">今日新增</span>-->
              </div>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="4">
        <a-card class="stat-card exercise-card" hoverable>
          <div class="stat-content">
            <div class="stat-icon">
              <ThunderboltOutlined />
            </div>
            <div class="stat-info">
          <a-statistic
            title="运动记录"
            :value="stats.totalExerciseRecords"
                :value-style="{ color: '#fa8c16', fontSize: '28px', fontWeight: 'bold' }"
            suffix="条"
              />
              <div class="stat-trend">
                <span class="trend-text positive">+{{ stats.todayExerciseRecords }}</span>
<!--                <span class="trend-label">今日新增</span>-->
              </div>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="4">
        <a-card class="stat-card weight-card" hoverable>
          <div class="stat-content">
            <div class="stat-icon">
              <LineChartOutlined />
            </div>
            <div class="stat-info">
              <a-statistic
                title="体重记录"
                :value="stats.totalWeightRecords"
                :value-style="{ color: '#722ed1', fontSize: '28px', fontWeight: 'bold' }"
                suffix="条"
              />
              <div class="stat-trend">
                <span class="trend-text positive">+{{ stats.todayWeightRecords }}</span>
<!--                <span class="trend-label">今日新增</span>-->
              </div>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="4">
        <a-card class="stat-card recipe-card" hoverable>
          <div class="stat-content">
            <div class="stat-icon">
              <BookOutlined />
            </div>
            <div class="stat-info">
              <a-statistic
                title="食谱"
                :value="stats.totalRecipes"
                :value-style="{ color: '#eb2f96', fontSize: '28px', fontWeight: 'bold' }"
                suffix="个"
              />
              <div class="stat-trend">
                <span class="trend-text positive">+{{ stats.todayRecipes }}</span>
<!--                <span class="trend-label">今日新增</span>-->
              </div>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="4">
        <a-card class="stat-card food-card" hoverable>
          <div class="stat-content">
            <div class="stat-icon">
              <AppleOutlined />
            </div>
            <div class="stat-info">
              <a-statistic
                title="食物"
                :value="stats.totalFoods"
                :value-style="{ color: '#13c2c2', fontSize: '28px', fontWeight: 'bold' }"
                suffix="种"
              />
              <div class="stat-trend">
                <span class="trend-text positive">+{{ stats.todayFoods }}</span>
<!--                <span class="trend-label">今日新增</span>-->
              </div>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 图表区域 -->
    <a-row :gutter="20" class="chart-row">
      <a-col :span="8">
        <a-card title="用户增长趋势" class="chart-card" :bordered="false">
          <template #extra>
            <a-button type="link" size="small">
              <BarChartOutlined />
            </a-button>
          </template>
          <div ref="userTrendChart" class="chart-container">
            <div class="chart-content">
              <div class="chart-legend">
                <span class="legend-item">
                  <span class="legend-color" style="background: #1890ff;"></span>
                  <span>总用户数</span>
                </span>
                <span class="legend-item">
                  <span class="legend-color" style="background: #52c41a;"></span>
                  <span>新增用户</span>
                </span>
              </div>
              <div class="chart-bars">
                <div v-for="(item, index) in userTrendData" :key="index" class="bar-item">
                  <div class="bar-label">{{ item.date }}</div>
                  <div class="bar-container">
                    <div class="bar bar-total" :style="{ height: (item.total / Math.max(...userTrendData.map(d => d.total))) * 100 + '%' }"></div>
                    <div class="bar bar-new" :style="{ height: (item.new / Math.max(...userTrendData.map(d => d.new))) * 100 + '%' }"></div>
                  </div>
                  <div class="bar-values">
                    <div class="value">{{ item.total }}</div>
                    <div class="value">{{ item.new }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="8">
        <a-card title="数据分布" class="chart-card" :bordered="false">
          <template #extra>
            <a-button type="link" size="small">
              <PieChartOutlined />
            </a-button>
          </template>
          <div ref="dataDistributionChart" class="chart-container">
            <div class="chart-content">
              <div class="pie-chart">
                <div class="pie-container">
                  <div class="pie-slice slice-1" :style="{ transform: `rotate(${dataDistributionData[0]?.angle || 0}deg)` }"></div>
                  <div class="pie-slice slice-2" :style="{ transform: `rotate(${dataDistributionData[1]?.angle || 0}deg)` }"></div>
                  <div class="pie-slice slice-3" :style="{ transform: `rotate(${dataDistributionData[2]?.angle || 0}deg)` }"></div>
                  <div class="pie-slice slice-4" :style="{ transform: `rotate(${dataDistributionData[3]?.angle || 0}deg)` }"></div>
                </div>
                <div class="pie-center">
                  <div class="pie-center-text">总计</div>
                  <div class="pie-center-value">{{ dataDistributionData.reduce((sum, item) => sum + item.value, 0) }}</div>
                </div>
              </div>
              <div class="pie-legend">
                <div v-for="(item, index) in dataDistributionData" :key="index" class="pie-legend-item">
                  <span class="pie-legend-color" :style="{ background: item.color }"></span>
                  <span class="pie-legend-label">{{ item.name }}</span>
                  <span class="pie-legend-value">{{ item.value }}</span>
                  <span class="pie-legend-percent">{{ item.percent }}%</span>
                </div>
              </div>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="8">
        <a-card title="活跃度统计" class="chart-card" :bordered="false">
          <template #extra>
            <a-button type="link" size="small">
              <LineChartOutlined />
            </a-button>
          </template>
          <div ref="activityChart" class="chart-container">
            <div class="chart-content">
              <div class="line-chart">
                <div class="line-chart-legend">
                  <span class="legend-item">
                    <span class="legend-color" style="background: #fa8c16;"></span>
                    <span>活跃用户</span>
                  </span>
                  <span class="legend-item">
                    <span class="legend-color" style="background: #13c2c2;"></span>
                    <span>新增用户</span>
                  </span>
                </div>
                <div class="line-chart-container">
                  <div class="line-chart-grid">
                    <div v-for="i in 5" :key="i" class="grid-line"></div>
                  </div>
                  <div class="line-chart-lines">
                    <svg class="line-svg" viewBox="0 0 200 120">
                      <polyline
                        :points="activityData.map((item, index) => `${index * 40},${120 - (item.active / Math.max(...activityData.map(d => d.active))) * 100}`).join(' ')"
                        fill="none"
                        stroke="#fa8c16"
                        stroke-width="2"
                      />
                      <polyline
                        :points="activityData.map((item, index) => `${index * 40},${120 - (item.new / Math.max(...activityData.map(d => d.new))) * 100}`).join(' ')"
                        fill="none"
                        stroke="#13c2c2"
                        stroke-width="2"
                      />
                    </svg>
                  </div>
                  <div class="line-chart-points">
                    <div v-for="(item, index) in activityData" :key="index" class="line-point">
                      <div class="point-label">{{ item.date }}</div>
                      <div class="point-values">
                        <div class="point-value active">{{ item.active }}</div>
                        <div class="point-value new">{{ item.new }}</div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 数据表格 -->
    <a-row :gutter="20" class="table-row">
      <a-col :span="8">
        <a-card title="最新用户" class="table-card" :bordered="false">
          <template #extra>
            <a-button type="link" size="small" @click="$router.push('/users')">
              查看全部
            </a-button>
          </template>
          <a-table
            :columns="userColumns"
            :data-source="recentUsers"
            :pagination="false"
            size="small"
            :show-header="false"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'avatar'">
                <a-avatar :src="record.avatar" :size="32">
                  {{ record.nickname?.charAt(0) }}
                </a-avatar>
              </template>
              <template v-else-if="column.key === 'createdAt'">
                <span class="time-text">{{ formatDate(record.createdAt) }}</span>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>
      <a-col :span="8">
        <a-card title="最新饮食记录" class="table-card" :bordered="false">
          <template #extra>
            <a-button type="link" size="small" @click="$router.push('/diet-records')">
              查看全部
            </a-button>
          </template>
          <a-table
            :columns="dietColumns"
            :data-source="recentDietRecords"
            :pagination="false"
            size="small"
            :show-header="false"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'user'">
                <span>{{ record.user?.nickname }}</span>
              </template>
              <template v-else-if="column.key === 'mealType'">
                <a-tag :color="getMealTypeColor(record.mealType)" size="small">
                  {{ record.mealType }}
                </a-tag>
              </template>
              <template v-else-if="column.key === 'recordedAt'">
                <span class="time-text">{{ formatDate(record.recordedAt) }}</span>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>
      <a-col :span="8">
        <a-card title="最新运动记录" class="table-card" :bordered="false">
          <template #extra>
            <a-button type="link" size="small" @click="$router.push('/exercise-records')">
              查看全部
            </a-button>
          </template>
          <a-table
            :columns="exerciseColumns"
            :data-source="recentExerciseRecords"
            :pagination="false"
            size="small"
            :show-header="false"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'user'">
                <span>{{ record.user?.nickname }}</span>
              </template>
              <template v-else-if="column.key === 'exerciseType'">
                <a-tag :color="getExerciseTypeColor(record.exerciseType)" size="small">
                  {{ record.exerciseType }}
                </a-tag>
              </template>
              <template v-else-if="column.key === 'exerciseTime'">
                <span class="time-text">{{ formatDate(record.exerciseTime) }}</span>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>
    </a-row>

    <!-- 快速操作 -->
    <a-row :gutter="20" class="quick-actions-row">
      <a-col :span="24">
        <a-card title="快速操作" class="quick-actions-card" :bordered="false">
          <a-row :gutter="16">
            <a-col :span="4">
              <a-button type="primary" block size="large" @click="$router.push('/users')">
                <UserOutlined />
                用户管理
              </a-button>
            </a-col>
            <a-col :span="4">
              <a-button type="default" block size="large" @click="$router.push('/diet-records')">
                <CoffeeOutlined />
                饮食记录
              </a-button>
            </a-col>
            <a-col :span="4">
              <a-button type="default" block size="large" @click="$router.push('/exercise-records')">
                <ThunderboltOutlined />
                运动记录
              </a-button>
            </a-col>
            <a-col :span="4">
              <a-button type="default" block size="large" @click="$router.push('/weight-records')">
                <LineChartOutlined />
                体重记录
              </a-button>
            </a-col>
            <a-col :span="4">
              <a-button type="default" block size="large" @click="$router.push('/recipes')">
                <BookOutlined />
                食谱管理
              </a-button>
            </a-col>
            <a-col :span="4">
              <a-button type="default" block size="large" @click="$router.push('/foods')">
                <AppleOutlined />
                食物管理
              </a-button>
            </a-col>
          </a-row>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import {
  UserOutlined,
  UserAddOutlined,
  CoffeeOutlined,
  ThunderboltOutlined,
  DashboardOutlined,
  ReloadOutlined,
  BarChartOutlined,
  PieChartOutlined,
  LineChartOutlined,
  BookOutlined,
  AppleOutlined,
} from '@ant-design/icons-vue'
import { getUserStats, getUserList } from '@/api/user'
import { getDietRecordStats, getDietRecordList } from '@/api/dietRecord'
import { getExerciseRecordStats, getExerciseRecordList } from '@/api/exerciseRecord'
import { getWeightRecordStats, getWeightRecordList } from '@/api/weightRecord'
import { getFoodStats, getFoodList } from '@/api/food'
import { getRecipeStats, getRecipeList } from '@/api/recipe'
import dayjs from 'dayjs'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const dateRange = ref<[dayjs.Dayjs, dayjs.Dayjs]>([
  dayjs().subtract(7, 'day'),
  dayjs()
])

const stats = reactive({
  totalUsers: 0,
  newUsersToday: 0,
  totalDietRecords: 0,
  todayDietRecords: 0,
  totalExerciseRecords: 0,
  todayExerciseRecords: 0,
  totalWeightRecords: 0,
  todayWeightRecords: 0,
  totalRecipes: 0,
  todayRecipes: 0,
  totalFoods: 0,
  todayFoods: 0,
})

const recentUsers = ref<any[]>([])
const recentDietRecords = ref<any[]>([])
const recentExerciseRecords = ref<any[]>([])

// 图表数据
const userTrendData = ref<any[]>([])
const dataDistributionData = ref<any[]>([])
const activityData = ref<any[]>([])

// 图表引用
const userTrendChart = ref<HTMLElement>()
const dataDistributionChart = ref<HTMLElement>()
const activityChart = ref<HTMLElement>()

// 用户表格列配置
const userColumns = [
  {
    title: '头像',
    dataIndex: 'avatar',
    key: 'avatar',
    width: 60,
  },
  {
    title: '昵称',
    dataIndex: 'nickname',
    key: 'nickname',
  },
  {
    title: '注册时间',
    dataIndex: 'createdAt',
    key: 'createdAt',
    width: 120,
  },
]

// 饮食记录表格列配置
const dietColumns = [
  {
    title: '用户',
    dataIndex: 'user',
    key: 'user',
  },
  {
    title: '餐别',
    dataIndex: 'mealType',
    key: 'mealType',
    width: 80,
  },
  {
    title: '记录时间',
    dataIndex: 'recordedAt',
    key: 'recordedAt',
    width: 120,
  },
]

// 运动记录表格列配置
const exerciseColumns = [
  {
    title: '用户',
    dataIndex: 'user',
    key: 'user',
  },
  {
    title: '运动类型',
    dataIndex: 'exerciseType',
    key: 'exerciseType',
    width: 100,
  },
  {
    title: '运动时间',
    dataIndex: 'exerciseTime',
    key: 'exerciseTime',
    width: 120,
  },
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

// 获取运动类型颜色
const getExerciseTypeColor = (exerciseType: string) => {
  switch (exerciseType) {
    case '跑步': return 'red'
    case '游泳': return 'blue'
    case '瑜伽': return 'green'
    case '健身': return 'purple'
    default: return 'default'
  }
}

// 格式化日期
const formatDate = (date: string) => {
  return dayjs(date).format('MM-DD HH:mm')
}

// 加载统计数据
const loadStats = async () => {
  try {
    loading.value = true
    
    // 并行调用所有统计API
    const [
      userStatsResult,
      dietStatsResult,
      exerciseStatsResult,
      weightStatsResult,
      foodStatsResult,
      recipeStatsResult
    ] = await Promise.allSettled([
      getUserStats(),
      getDietRecordStats(),
      getExerciseRecordStats(),
      getWeightRecordStats(),
      getFoodStats(),
      getRecipeStats()
    ])
    
    // 处理用户统计数据
    if (userStatsResult.status === 'fulfilled') {
      const userData = userStatsResult.value.data
      Object.assign(stats, {
        totalUsers: userData.totalUsers || 0,
        newUsersToday: userData.newUsersToday || 0,
      })
    }
    
    // 处理饮食记录统计数据
    if (dietStatsResult.status === 'fulfilled') {
      const dietData = dietStatsResult.value.data
      Object.assign(stats, {
        totalDietRecords: dietData.totalDietRecords || 0,
        todayDietRecords: dietData.todayDietRecords || 0,
      })
    }
    
    // 处理运动记录统计数据
    if (exerciseStatsResult.status === 'fulfilled') {
      const exerciseData = exerciseStatsResult.value.data
      Object.assign(stats, {
        totalExerciseRecords: exerciseData.totalExerciseRecords || 0,
        todayExerciseRecords: exerciseData.todayExerciseRecords || 0,
      })
    }
    
    // 处理体重记录统计数据
    if (weightStatsResult.status === 'fulfilled') {
      const weightData = weightStatsResult.value.data
      Object.assign(stats, {
        totalWeightRecords: weightData.totalWeightRecords || 0,
        todayWeightRecords: weightData.todayWeightRecords || 0,
      })
    }
    
    // 处理食物统计数据
    if (foodStatsResult.status === 'fulfilled') {
      const foodData = foodStatsResult.value.data
      Object.assign(stats, {
        totalFoods: foodData.totalFoods || 0,
        todayFoods: foodData.todayFoods || 0,
      })
    }
    
    // 处理食谱统计数据
    if (recipeStatsResult.status === 'fulfilled') {
      const recipeData = recipeStatsResult.value.data
      Object.assign(stats, {
        totalRecipes: recipeData.totalRecipes || 0,
        todayRecipes: recipeData.todayRecipes || 0,
      })
    }
    
    // 如果某些API调用失败，使用默认值
    if (userStatsResult.status === 'rejected') {
      console.warn('用户统计API调用失败:', userStatsResult.reason)
      Object.assign(stats, {
        totalUsers: stats.totalUsers || 0,
        newUsersToday: stats.newUsersToday || 0,
      })
    }
    
    if (dietStatsResult.status === 'rejected') {
      console.warn('饮食记录统计API调用失败:', dietStatsResult.reason)
      Object.assign(stats, {
        totalDietRecords: stats.totalDietRecords || 0,
        todayDietRecords: stats.todayDietRecords || 0,
      })
    }
    
    if (exerciseStatsResult.status === 'rejected') {
      console.warn('运动记录统计API调用失败:', exerciseStatsResult.reason)
      Object.assign(stats, {
        totalExerciseRecords: stats.totalExerciseRecords || 0,
        todayExerciseRecords: stats.todayExerciseRecords || 0,
      })
    }
    
    if (weightStatsResult.status === 'rejected') {
      console.warn('体重记录统计API调用失败:', weightStatsResult.reason)
      Object.assign(stats, {
        totalWeightRecords: stats.totalWeightRecords || 0,
        todayWeightRecords: stats.todayWeightRecords || 0,
      })
    }
    
    if (foodStatsResult.status === 'rejected') {
      console.warn('食物统计API调用失败:', foodStatsResult.reason)
      Object.assign(stats, {
        totalFoods: stats.totalFoods || 0,
        todayFoods: stats.todayFoods || 0,
      })
    }
    
    if (recipeStatsResult.status === 'rejected') {
      console.warn('食谱统计API调用失败:', recipeStatsResult.reason)
      Object.assign(stats, {
        totalRecipes: stats.totalRecipes || 0,
        todayRecipes: stats.todayRecipes || 0,
      })
    }
    
  } catch (error) {
    console.error('加载统计数据失败:', error)
    // 使用默认值
    Object.assign(stats, {
      totalUsers: 0,
      newUsersToday: 0,
      totalDietRecords: 0,
      todayDietRecords: 0,
      totalExerciseRecords: 0,
      todayExerciseRecords: 0,
      totalWeightRecords: 0,
      todayWeightRecords: 0,
      totalRecipes: 0,
      todayRecipes: 0,
      totalFoods: 0,
      todayFoods: 0,
    })
  } finally {
    loading.value = false
  }
}

// 加载图表数据
const loadChartData = () => {
  // 用户增长趋势数据
  userTrendData.value = [
    { date: '01-01', total: 1200, new: 45 },
    { date: '01-02', total: 1230, new: 30 },
    { date: '01-03', total: 1265, new: 35 },
    { date: '01-04', total: 1280, new: 15 },
    { date: '01-05', total: 1310, new: 30 },
    { date: '01-06', total: 1345, new: 35 },
    { date: '01-07', total: 1376, new: 31 },
  ]

  // 数据分布饼图数据
  const total = stats.totalUsers + stats.totalDietRecords + stats.totalExerciseRecords + stats.totalWeightRecords
  dataDistributionData.value = [
    {
      name: '用户数',
      value: stats.totalUsers,
      percent: Math.round((stats.totalUsers / total) * 100),
      color: '#1890ff',
      angle: 0
    },
    {
      name: '饮食记录',
      value: stats.totalDietRecords,
      percent: Math.round((stats.totalDietRecords / total) * 100),
      color: '#52c41a',
      angle: Math.round((stats.totalUsers / total) * 360)
    },
    {
      name: '运动记录',
      value: stats.totalExerciseRecords,
      percent: Math.round((stats.totalExerciseRecords / total) * 100),
      color: '#fa8c16',
      angle: Math.round(((stats.totalUsers + stats.totalDietRecords) / total) * 360)
    },
    {
      name: '体重记录',
      value: stats.totalWeightRecords,
      percent: Math.round((stats.totalWeightRecords / total) * 100),
      color: '#722ed1',
      angle: Math.round(((stats.totalUsers + stats.totalDietRecords + stats.totalExerciseRecords) / total) * 360)
    }
  ]

  // 活跃度统计数据
  activityData.value = [
    { date: '01-01', active: 890, new: 45 },
    { date: '01-02', active: 920, new: 30 },
    { date: '01-03', active: 950, new: 35 },
    { date: '01-04', active: 980, new: 15 },
    { date: '01-05', active: 1010, new: 30 },
    { date: '01-06', active: 1045, new: 35 },
    { date: '01-07', active: 1076, new: 31 },
  ]
}

// 加载最近数据
const loadRecentData = async () => {
  try {
    // 并行加载最近的数据
    const [usersResult, dietRecordsResult, exerciseRecordsResult] = await Promise.allSettled([
      getUserList({ page: 1, size: 5 }),
      getDietRecordList({ page: 1, size: 5 }),
      getExerciseRecordList({ page: 1, size: 5 })
    ])
    
    // 处理最近用户数据
    if (usersResult.status === 'fulfilled') {
      recentUsers.value = usersResult.value.data.records.map(user => ({
        id: user.id,
        nickname: user.nickname,
        avatar: user.avatar || '',
        createdAt: user.createdAt
      }))
    } else {
      console.warn('获取最近用户数据失败:', usersResult.reason)
      // 使用测试数据来演示滚动条
      recentUsers.value = [
        { id: 1, nickname: '用户001', avatar: '', createdAt: dayjs().subtract(1, 'hour').toISOString() },
        { id: 2, nickname: '用户002', avatar: '', createdAt: dayjs().subtract(2, 'hour').toISOString() },
        { id: 3, nickname: '用户003', avatar: '', createdAt: dayjs().subtract(3, 'hour').toISOString() },
        { id: 4, nickname: '用户004', avatar: '', createdAt: dayjs().subtract(4, 'hour').toISOString() },
        { id: 5, nickname: '用户005', avatar: '', createdAt: dayjs().subtract(5, 'hour').toISOString() },
        { id: 6, nickname: '用户006', avatar: '', createdAt: dayjs().subtract(6, 'hour').toISOString() },
        { id: 7, nickname: '用户007', avatar: '', createdAt: dayjs().subtract(7, 'hour').toISOString() },
        { id: 8, nickname: '用户008', avatar: '', createdAt: dayjs().subtract(8, 'hour').toISOString() },
        { id: 9, nickname: '用户009', avatar: '', createdAt: dayjs().subtract(9, 'hour').toISOString() },
        { id: 10, nickname: '用户010', avatar: '', createdAt: dayjs().subtract(10, 'hour').toISOString() },
      ]
    }
    
    // 处理最近饮食记录数据
    if (dietRecordsResult.status === 'fulfilled') {
      recentDietRecords.value = dietRecordsResult.value.data.records.map(record => ({
        id: record.id,
        user: record.user ? { nickname: record.user.nickname } : { nickname: '未知用户' },
        mealType: record.mealType,
        recordedAt: record.recordedAt
      }))
    } else {
      console.warn('获取最近饮食记录失败:', dietRecordsResult.reason)
      // 使用测试数据来演示滚动条
      recentDietRecords.value = [
        { id: 1, user: { nickname: '用户001' }, mealType: '早餐', recordedAt: dayjs().subtract(30, 'minute').toISOString() },
        { id: 2, user: { nickname: '用户002' }, mealType: '午餐', recordedAt: dayjs().subtract(1, 'hour').toISOString() },
        { id: 3, user: { nickname: '用户003' }, mealType: '晚饭', recordedAt: dayjs().subtract(2, 'hour').toISOString() },
        { id: 4, user: { nickname: '用户004' }, mealType: '加餐', recordedAt: dayjs().subtract(3, 'hour').toISOString() },
        { id: 5, user: { nickname: '用户005' }, mealType: '早餐', recordedAt: dayjs().subtract(4, 'hour').toISOString() },
        { id: 6, user: { nickname: '用户006' }, mealType: '午餐', recordedAt: dayjs().subtract(5, 'hour').toISOString() },
        { id: 7, user: { nickname: '用户007' }, mealType: '晚饭', recordedAt: dayjs().subtract(6, 'hour').toISOString() },
        { id: 8, user: { nickname: '用户008' }, mealType: '加餐', recordedAt: dayjs().subtract(7, 'hour').toISOString() },
        { id: 9, user: { nickname: '用户009' }, mealType: '早餐', recordedAt: dayjs().subtract(8, 'hour').toISOString() },
        { id: 10, user: { nickname: '用户010' }, mealType: '午餐', recordedAt: dayjs().subtract(9, 'hour').toISOString() },
      ]
    }
    
    // 处理最近运动记录数据
    if (exerciseRecordsResult.status === 'fulfilled') {
      recentExerciseRecords.value = exerciseRecordsResult.value.data.records.map(record => ({
        id: record.id,
        user: record.user ? { nickname: record.user.nickname } : { nickname: '未知用户' },
        exerciseType: record.exerciseType,
        exerciseTime: record.exerciseTime
      }))
    } else {
      console.warn('获取最近运动记录失败:', exerciseRecordsResult.reason)
      // 使用测试数据来演示滚动条
      recentExerciseRecords.value = [
        { id: 1, user: { nickname: '用户001' }, exerciseType: '跑步', exerciseTime: dayjs().subtract(45, 'minute').toISOString() },
        { id: 2, user: { nickname: '用户002' }, exerciseType: '健身', exerciseTime: dayjs().subtract(1, 'hour').toISOString() },
        { id: 3, user: { nickname: '用户003' }, exerciseType: '瑜伽', exerciseTime: dayjs().subtract(2, 'hour').toISOString() },
        { id: 4, user: { nickname: '用户004' }, exerciseType: '游泳', exerciseTime: dayjs().subtract(3, 'hour').toISOString() },
        { id: 5, user: { nickname: '用户005' }, exerciseType: '跑步', exerciseTime: dayjs().subtract(4, 'hour').toISOString() },
        { id: 6, user: { nickname: '用户006' }, exerciseType: '健身', exerciseTime: dayjs().subtract(5, 'hour').toISOString() },
        { id: 7, user: { nickname: '用户007' }, exerciseType: '瑜伽', exerciseTime: dayjs().subtract(6, 'hour').toISOString() },
        { id: 8, user: { nickname: '用户008' }, exerciseType: '游泳', exerciseTime: dayjs().subtract(7, 'hour').toISOString() },
        { id: 9, user: { nickname: '用户009' }, exerciseType: '跑步', exerciseTime: dayjs().subtract(8, 'hour').toISOString() },
        { id: 10, user: { nickname: '用户010' }, exerciseType: '健身', exerciseTime: dayjs().subtract(9, 'hour').toISOString() },
      ]
    }
    
  } catch (error) {
    console.error('加载最近数据失败:', error)
    // 设置空数组作为默认值
    recentUsers.value = []
    recentDietRecords.value = []
    recentExerciseRecords.value = []
  }
}

// 刷新数据
const refreshData = async () => {
  await Promise.all([loadStats(), loadRecentData()])
  loadChartData()
}

// 日期范围变化
const onDateRangeChange = () => {
  loadStats()
  loadChartData()
}

// 初始化
onMounted(async () => {
  await Promise.all([loadStats(), loadRecentData()])
  loadChartData()
})
</script>

<style scoped>
.dashboard {
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding: 20px 0;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #1f2937;
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 16px;
  align-items: center;
}

.stats-row {
  margin-bottom: 32px;
}

.stat-card {
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: none;
  overflow: hidden;
  transition: all 0.3s ease;
  background: white;
  min-height: 250px;
  height: 250px;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.stat-content {
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  height: 100%;
}

.stat-content :deep(.ant-statistic-title) {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 8px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
  flex-shrink: 0;
}

.user-card .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.diet-card .stat-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.exercise-card .stat-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.weight-card .stat-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.recipe-card .stat-icon {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.food-card .stat-icon {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
}

.stat-info {
  flex: 1;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
}

.trend-text {
  font-weight: 600;
  font-size: 14px;
}

.trend-text.positive {
  color: #52c41a;
}

.trend-label {
  color: #8c8c8c;
  font-size: 12px;
}

.chart-row {
  margin-bottom: 32px;
}

.chart-card {
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: none;
  height: 320px;
}

.chart-container {
  height: 220px;
  width: 100%;
  padding: 16px;
}

.chart-content {
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* 柱状图样式 */
.chart-legend {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
  justify-content: center;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #666;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.chart-bars {
  display: flex;
  align-items: end;
  justify-content: space-between;
  height: 120px;
  gap: 8px;
}

.bar-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.bar-label {
  font-size: 10px;
  color: #999;
  transform: rotate(-45deg);
  white-space: nowrap;
}

.bar-container {
  position: relative;
  width: 100%;
  height: 80px;
  display: flex;
  align-items: end;
  gap: 2px;
}

.bar {
  width: 100%;
  border-radius: 2px 2px 0 0;
  min-height: 4px;
  transition: all 0.3s ease;
}

.bar-total {
  background: linear-gradient(135deg, #1890ff, #40a9ff);
}

.bar-new {
  background: linear-gradient(135deg, #52c41a, #73d13d);
}

.bar-values {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}

.value {
  font-size: 10px;
  color: #666;
}

/* 饼图样式 */
.pie-chart {
  display: flex;
  align-items: center;
  gap: 20px;
  height: 120px;
}

.pie-container {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: conic-gradient(
    #1890ff 0deg 90deg,
    #52c41a 90deg 180deg,
    #fa8c16 180deg 270deg,
    #722ed1 270deg 360deg
  );
}

.pie-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 60px;
  height: 60px;
  background: white;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.pie-center-text {
  font-size: 10px;
  color: #999;
}

.pie-center-value {
  font-size: 14px;
  font-weight: bold;
  color: #333;
}

.pie-legend {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.pie-legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.pie-legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.pie-legend-label {
  flex: 1;
  color: #666;
}

.pie-legend-value {
  color: #333;
  font-weight: 500;
}

.pie-legend-percent {
  color: #999;
  width: 30px;
  text-align: right;
}

/* 折线图样式 */
.line-chart {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.line-chart-legend {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
  justify-content: center;
}

.line-chart-container {
  position: relative;
  flex: 1;
  height: 120px;
}

.line-chart-grid {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.grid-line {
  height: 1px;
  background: #f0f0f0;
}

.line-chart-lines {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.line-svg {
  width: 100%;
  height: 100%;
}

.line-chart-points {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: space-between;
  align-items: end;
}

.line-point {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.point-label {
  font-size: 10px;
  color: #999;
  transform: rotate(-45deg);
}

.point-values {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}

.point-value {
  font-size: 10px;
  padding: 2px 4px;
  border-radius: 2px;
  color: white;
}

.point-value.active {
  background: #fa8c16;
}

.point-value.new {
  background: #13c2c2;
}

.table-row {
  margin-bottom: 32px;
}

.table-card {
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: none;
  height: 320px;
  overflow: hidden;
}

.table-card :deep(.ant-card-body) {
  height: 280px;
  padding: 16px;
  overflow-y: auto;
}

.table-card :deep(.ant-card-body)::-webkit-scrollbar {
  width: 6px;
}

.table-card :deep(.ant-card-body)::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.table-card :deep(.ant-card-body)::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.table-card :deep(.ant-card-body)::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.table-card :deep(.ant-table) {
  margin: 0;
}

.table-card :deep(.ant-table-tbody) {
  max-height: 240px;
  overflow-y: auto;
}

.table-card :deep(.ant-table-tbody)::-webkit-scrollbar {
  width: 4px;
}

.table-card :deep(.ant-table-tbody)::-webkit-scrollbar-track {
  background: transparent;
}

.table-card :deep(.ant-table-tbody)::-webkit-scrollbar-thumb {
  background: #d9d9d9;
  border-radius: 2px;
}

.table-card :deep(.ant-table-tbody)::-webkit-scrollbar-thumb:hover {
  background: #bfbfbf;
}

/* Firefox 滚动条样式 */
.table-card :deep(.ant-card-body) {
  scrollbar-width: thin;
  scrollbar-color: #c1c1c1 #f1f1f1;
}

.table-card :deep(.ant-table-tbody) {
  scrollbar-width: thin;
  scrollbar-color: #d9d9d9 transparent;
}

/* 确保表格行高度一致 */
.table-card :deep(.ant-table-tbody > tr > td) {
  padding: 8px 12px;
  border-bottom: 1px solid #f0f0f0;
}

.table-card :deep(.ant-table-tbody > tr:hover > td) {
  background: #fafafa;
}

/* 优化表格内容的显示 */
.table-card :deep(.ant-table-tbody > tr) {
  height: 48px;
}

.time-text {
  color: #8c8c8c;
  font-size: 12px;
}

.quick-actions-row {
  margin-bottom: 32px;
}

.quick-actions-card {
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: none;
  padding: 24px;
}

.quick-actions-card :deep(.ant-btn) {
  height: 60px;
  border-radius: 12px;
  font-weight: 600;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 14px;
}

.quick-actions-card :deep(.ant-btn-primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.quick-actions-card :deep(.ant-btn-primary:hover) {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
}

.quick-actions-card :deep(.ant-btn-default) {
  background: white;
  border: 2px solid #f0f0f0;
  color: #595959;
}

.quick-actions-card :deep(.ant-btn-default:hover) {
  border-color: #d9d9d9;
  color: #1890ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .stat-content {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }

  .stat-icon {
    width: 48px;
    height: 48px;
    font-size: 20px;
  }

  .quick-actions-card :deep(.ant-btn) {
    height: 50px;
    font-size: 12px;
  }
}

@media (max-width: 768px) {
  .dashboard {
    padding: 16px;
  }

  .dashboard-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }

  .header-actions {
    width: 100%;
    justify-content: space-between;
  }

  .page-title {
    font-size: 24px;
  }

  .stat-content {
    padding: 16px;
  }

  .chart-card,
  .table-card {
    height: auto;
    min-height: 280px;
  }

  .chart-container {
    height: 180px;
  }
}
</style>

