/**
 * 仪表盘API测试脚本
 * 用于测试后端统计接口是否正常工作
 */

const axios = require('axios');

// 配置基础URL（请根据实际情况修改）
const BASE_URL = 'http://localhost:8080';

// 测试所有统计接口
async function testDashboardAPIs() {
  const apis = [
    { name: '用户统计', url: '/admin/users/stats' },
    { name: '饮食记录统计', url: '/admin/diet-records/stats' },
    { name: '运动记录统计', url: '/admin/exercise-records/stats' },
    { name: '体重记录统计', url: '/admin/weight-records/stats' },
    { name: '食物统计', url: '/admin/foods/stats' },
    { name: '食谱统计', url: '/admin/recipes/stats' }
  ];

  console.log('开始测试仪表盘API接口...\n');

  for (const api of apis) {
    try {
      console.log(`测试 ${api.name} 接口: ${api.url}`);
      const response = await axios.get(`${BASE_URL}${api.url}`);
      
      if (response.status === 200) {
        console.log(`✅ ${api.name} 接口正常`);
        console.log(`   返回数据:`, JSON.stringify(response.data, null, 2));
      } else {
        console.log(`❌ ${api.name} 接口异常 - 状态码: ${response.status}`);
      }
    } catch (error) {
      if (error.response) {
        console.log(`❌ ${api.name} 接口错误 - 状态码: ${error.response.status}`);
        console.log(`   错误信息:`, error.response.data);
      } else {
        console.log(`❌ ${api.name} 接口网络错误:`, error.message);
      }
    }
    console.log('');
  }

  // 测试数据列表接口
  console.log('测试数据列表接口...\n');
  
  const listAPIs = [
    { name: '用户列表', url: '/admin/users?page=1&size=5' },
    { name: '饮食记录列表', url: '/admin/diet-records?page=1&size=5' },
    { name: '运动记录列表', url: '/admin/exercise-records?page=1&size=5' }
  ];

  for (const api of listAPIs) {
    try {
      console.log(`测试 ${api.name} 接口: ${api.url}`);
      const response = await axios.get(`${BASE_URL}${api.url}`);
      
      if (response.status === 200) {
        console.log(`✅ ${api.name} 接口正常`);
        console.log(`   返回记录数:`, response.data.data?.records?.length || 0);
      } else {
        console.log(`❌ ${api.name} 接口异常 - 状态码: ${response.status}`);
      }
    } catch (error) {
      if (error.response) {
        console.log(`❌ ${api.name} 接口错误 - 状态码: ${error.response.status}`);
        console.log(`   错误信息:`, error.response.data);
      } else {
        console.log(`❌ ${api.name} 接口网络错误:`, error.message);
      }
    }
    console.log('');
  }
}

// 运行测试
testDashboardAPIs().catch(console.error);

/**
 * 使用方法：
 * 1. 安装依赖：npm install axios
 * 2. 修改 BASE_URL 为实际的后端地址
 * 3. 运行脚本：node test_dashboard_apis.js
 */

