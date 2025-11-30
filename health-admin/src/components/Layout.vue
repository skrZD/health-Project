<template>
  <a-layout class="layout">
    <a-layout-sider
      v-model:collapsed="collapsed"
      :trigger="null"
      collapsible
      class="sider"
    >
      <div class="logo">
        <h2 v-if="!collapsed">健康管理中台</h2>
        <h2 v-else>健康</h2>
      </div>
      <a-menu
        v-model:selectedKeys="selectedKeys"
        mode="inline"
        theme="dark"
        :inline-collapsed="collapsed"
      >
        <a-menu-item key="dashboard" @click="navigateTo('/dashboard')">
          <template #icon>
            <DashboardOutlined />
          </template>
          仪表盘
        </a-menu-item>
        <a-menu-item key="users" @click="navigateTo('/users')">
          <template #icon>
            <UserOutlined />
          </template>
          用户管理
        </a-menu-item>
        <a-menu-item key="foods" @click="navigateTo('/foods')">
          <template #icon>
            <AppleOutlined />
          </template>
          食物管理
        </a-menu-item>
        <a-menu-item key="diet-records" @click="navigateTo('/diet-records')">
          <template #icon>
            <CoffeeOutlined />
          </template>
          饮食记录
        </a-menu-item>
        <a-menu-item key="exercise-records" @click="navigateTo('/exercise-records')">
          <template #icon>
            <ThunderboltOutlined />
          </template>
          运动记录
        </a-menu-item>
        <a-menu-item key="weight-records" @click="navigateTo('/weight-records')">
          <template #icon>
            <LineChartOutlined />
          </template>
          体重记录
        </a-menu-item>
        <a-menu-item key="recipes" @click="navigateTo('/recipes')">
          <template #icon>
            <BookOutlined />
          </template>
          食谱管理
        </a-menu-item>
      </a-menu>
    </a-layout-sider>
    
    <a-layout>
      <a-layout-header class="header">
        <div class="header-left">
          <a-button
            type="text"
            @click="collapsed = !collapsed"
            class="trigger"
          >
            <MenuUnfoldOutlined v-if="collapsed" />
            <MenuFoldOutlined v-else />
          </a-button>
        </div>
        
        <div class="header-right">
          <a-dropdown>
            <template #overlay>
              <a-menu>
                <a-menu-item key="profile">
                  <UserOutlined />
                  个人设置
                </a-menu-item>
                <a-menu-item key="logout" @click="handleLogout">
                  <LogoutOutlined />
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
            <a-button type="text" class="user-info">
              <UserOutlined />
              {{ userInfo?.displayName || '管理员' }}
            </a-button>
          </a-dropdown>
        </div>
      </a-layout-header>
      
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  DashboardOutlined,
  UserOutlined,
  AppleOutlined,
  CoffeeOutlined,
  ThunderboltOutlined,
  LineChartOutlined,
  BookOutlined,
  MenuUnfoldOutlined,
  MenuFoldOutlined,
  LogoutOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()

const collapsed = ref(false)
const userInfo = ref<any>(null)

// 根据当前路由设置选中的菜单项
const selectedKeys = computed(() => {
  const path = route.path
  if (path.startsWith('/dashboard')) return ['dashboard']
  if (path.startsWith('/users')) return ['users']
  if (path.startsWith('/foods')) return ['foods']
  if (path.startsWith('/diet-records')) return ['diet-records']
  if (path.startsWith('/exercise-records')) return ['exercise-records']
  if (path.startsWith('/weight-records')) return ['weight-records']
  if (path.startsWith('/recipes')) return ['recipes']
  return ['dashboard']
})

// 导航到指定页面
const navigateTo = (path: string) => {
  router.push(path)
}

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('admin_token')
  localStorage.removeItem('admin_user')
  message.success('已退出登录')
  router.push('/login')
}

// 初始化
onMounted(() => {
  const user = localStorage.getItem('admin_user')
  if (user) {
    userInfo.value = JSON.parse(user)
  }
})
</script>

<style scoped>
.layout {
  height: 100vh;
}

.sider {
  background: #001529;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #002140;
  color: white;
  font-weight: bold;
}

.header {
  background: white;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
}

.trigger {
  font-size: 18px;
  line-height: 64px;
  padding: 0 24px;
  cursor: pointer;
  transition: color 0.3s;
}

.trigger:hover {
  color: #1890ff;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.content {
  margin: 24px;
  padding: 24px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  overflow-y: auto;
}
</style>

