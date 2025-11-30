// 测试饮食记录API的脚本
const axios = require('axios');

const API_BASE = 'http://localhost:8088/api';

// 测试用户端API
async function testUserDietRecordsAPI() {
  console.log('🔍 测试用户端饮食记录API...');
  
  try {
    // 1. 先测试不需要认证的接口
    console.log('1. 测试健康检查接口...');
    const healthResponse = await axios.get(`${API_BASE}/health`);
    console.log('✅ 健康检查通过:', healthResponse.status);
    
    // 2. 测试认证接口
    console.log('2. 测试认证接口...');
    const authResponse = await axios.post(`${API_BASE}/auth/weixin/login`, {
      code: 'test_code'
    });
    console.log('✅ 认证接口响应:', authResponse.status);
    
    if (authResponse.data.code === 0) {
      const token = authResponse.data.data.token;
      console.log('✅ 获取到Token:', token.substring(0, 20) + '...');
      
      // 3. 使用Token测试饮食记录API
      console.log('3. 测试饮食记录创建...');
      const dietRecordData = {
        mealType: '早餐',
        recordedAt: new Date().toISOString(),
        planCalories: 600,
        note: '测试记录'
      };
      
      const dietResponse = await axios.post(`${API_BASE}/diet-records`, dietRecordData, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      
      console.log('✅ 饮食记录创建成功:', dietResponse.status);
      console.log('📊 响应数据:', dietResponse.data);
    }
    
  } catch (error) {
    console.error('❌ 用户端API测试失败:');
    if (error.response) {
      console.error('状态码:', error.response.status);
      console.error('错误信息:', error.response.data);
    } else {
      console.error('网络错误:', error.message);
    }
  }
}

// 测试管理端API
async function testAdminDietRecordsAPI() {
  console.log('\n🔍 测试管理端饮食记录API...');
  
  try {
    // 1. 测试管理员登录
    console.log('1. 测试管理员登录...');
    const adminLoginResponse = await axios.post(`${API_BASE}/admin/auth/login`, {
      username: 'admin',
      password: 'admin123'
    });
    
    console.log('✅ 管理员登录响应:', adminLoginResponse.status);
    
    if (adminLoginResponse.data.code === 0) {
      const adminToken = adminLoginResponse.data.data.token;
      console.log('✅ 获取到管理员Token:', adminToken.substring(0, 20) + '...');
      
      // 2. 测试管理端饮食记录列表
      console.log('2. 测试管理端饮食记录列表...');
      const listResponse = await axios.get(`${API_BASE}/admin/diet-records`, {
        headers: {
          'Authorization': `Bearer ${adminToken}`,
          'Content-Type': 'application/json'
        }
      });
      
      console.log('✅ 管理端饮食记录列表:', listResponse.status);
      console.log('📊 响应数据:', listResponse.data);
    }
    
  } catch (error) {
    console.error('❌ 管理端API测试失败:');
    if (error.response) {
      console.error('状态码:', error.response.status);
      console.error('错误信息:', error.response.data);
    } else {
      console.error('网络错误:', error.message);
    }
  }
}

// 运行测试
async function runTests() {
  console.log('🚀 开始测试饮食记录API...\n');
  
  await testUserDietRecordsAPI();
  await testAdminDietRecordsAPI();
  
  console.log('\n✅ 测试完成！');
}

// 如果直接运行此脚本
if (require.main === module) {
  runTests().catch(console.error);
}

module.exports = { testUserDietRecordsAPI, testAdminDietRecordsAPI };

