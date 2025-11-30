<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h1 class="title">健康管理系统中台</h1>
        <p class="subtitle">Health Management Admin</p>
      </div>
      
      <a-form
        :model="loginForm"
        :rules="rules"
        @finish="handleLogin"
        class="login-form"
        layout="vertical"
      >
        <a-form-item name="username" :rules="rules.username">
          <a-input
            v-model:value="loginForm.username"
            placeholder="请输入用户名"
            size="large"
            :prefix="h(UserOutlined)"
          />
        </a-form-item>
        
        <a-form-item name="password" :rules="rules.password">
          <a-input-password
            v-model:value="loginForm.password"
            placeholder="请输入密码"
            size="large"
            :prefix="h(LockOutlined)"
          />
        </a-form-item>
        
        <a-form-item>
          <a-checkbox v-model:checked="loginForm.remember">
            记住我
          </a-checkbox>
        </a-form-item>
        
        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            size="large"
            :loading="loading"
            block
          >
            登录
          </a-button>
        </a-form-item>
      </a-form>
      
      <div class="login-footer">
        <p class="copyright">© 2024 健康管理系统. All rights reserved.</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, h } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import { adminLogin, type LoginParams } from '@/api/auth'

const router = useRouter()
const loading = ref(false)

// 登录表单
const loginForm = reactive<LoginParams>({
  username: 'admin',
  password: 'admin123',
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' },
  ],
}

// 处理登录
const handleLogin = async () => {
  try {
    loading.value = true
    
    const result = await adminLogin(loginForm)
    
    if (result.code === 0) {
      // 保存登录信息
      localStorage.setItem('admin_token', result.data.token)
      localStorage.setItem('admin_user', JSON.stringify(result.data.user))
      
      message.success('登录成功')
      
      // 跳转到管理后台
      router.push('/dashboard')
    } else {
      message.error(result.message || '登录失败')
    }
  } catch (error: any) {
    message.error(error.message || '登录失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-box {
  width: 100%;
  max-width: 400px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  padding: 40px;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.title {
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.subtitle {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.login-form {
  margin-bottom: 24px;
}

.login-footer {
  text-align: center;
}

.copyright {
  font-size: 12px;
  color: #9ca3af;
  margin: 0;
}
</style>

