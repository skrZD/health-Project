// API配置文件
const API_CONFIG = {
  // 后端API基础地址
  BASE_URL: 'http://localhost:8088/api',
  
  // 各模块API地址
  AUTH: {
    LOGIN: '/auth/weixin/login',
    ME: '/auth/me',
    REFRESH: '/auth/refresh'
  },
  
  FOODS: {
    SEARCH: '/foods/search',
    CATEGORIES: '/food-categories'
  },
  
  DIET_RECORDS: {
    LIST: '/diet-records',
    CREATE: '/diet-records',
    UPDATE: '/diet-records',
    DELETE: '/diet-records',
    ITEMS: '/diet-records'
  },
  
  EXERCISE_RECORDS: {
    LIST: '/exercise-records',
    CREATE: '/exercise-records',
    UPDATE: '/exercise-records',
    DELETE: '/exercise-records',
    STATS: '/exercise-records/stats'
  },
  
  WEIGHT_RECORDS: {
    LIST: '/weight-records',
    CREATE: '/weight-records',
    UPDATE: '/weight-records',
    DELETE: '/weight-records',
    TREND: '/weight-records/trend',
    BMI: '/weight-records/bmi'
  },
  
  USERS: {
    DETAIL: '/users',
    UPDATE: '/users',
    STATS: '/users',
    GOALS: '/users'
  },
  
  RECIPES: {
    LIST: '/recipes',
    DETAIL: '/recipes',
    DETAIL_WITH_ITEMS: '/recipes',
    ITEMS: '/recipes',
    ENABLED: '/recipes/enabled',
    BY_CATEGORY: '/recipes/category'
  },
  
  HEALTH_PROFILES: {
    ME: '/health-profiles/me',
    UPDATE: '/health-profiles',
    BMI: '/health-profiles/bmi'
  },
  
  BMI_CALCULATIONS: {
    CALCULATE: '/bmi-calculations',
    MY_HISTORY: '/bmi-calculations/my-history'
  }
};

// 请求封装
const request = (options) => {
  return new Promise((resolve, reject) => {
    uni.request({
      url: API_CONFIG.BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        ...options.header
      },
      success: (res) => {
        if (res.data.code === 0) {
          resolve(res.data);
        } else {
          reject(res.data);
        }
      },
      fail: (err) => {
        reject(err);
      }
    });
  });
};

// 导出配置和请求方法
export default {
  API_CONFIG,
  request
};
