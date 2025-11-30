"use strict";
const common_vendor = require("../../common/vendor.js");
const qiunDataCharts = () => "../../uni_modules/qiun-data-charts/components/qiun-data-charts/qiun-data-charts.js";
const _sfc_main = {
  components: {
    qiunDataCharts
  },
  data() {
    return {
      showDietModal: false,
      mealTypes: ["早餐", "午餐", "晚饭", "加餐"],
      dietForm: {
        icon: "/static/fried_egg.png",
        type: "早餐",
        food_name: "",
        // 添加食物名称字段
        diet_num: 1,
        diet_kcal: 0,
        diet_plan_kcal: 600
      },
      chartData: {
        series: [
          {
            name: "活动小时数",
            data: 0.8
            // 14 / 12 = 1.16
          },
          {
            name: "锻炼时长",
            data: 1
            // 76 / 25 = 3.04
          },
          {
            name: "活动热量",
            data: 0.8
            // 538 / 516 = 1.04
          }
        ]
      },
      opts: {
        color: ["#F5296E", "#FFCC00", "#4A89FF"],
        padding: void 0,
        title: {
          name: "",
          fontSize: 35,
          color: "#1890ff"
        },
        subtitle: {
          name: "",
          fontSize: 15,
          color: "#666666"
        },
        extra: {
          arcbar: {
            type: "circle",
            width: 18,
            backgroundColor: "#E9E9E9",
            startAngle: 1.5,
            endAngle: 0.25,
            gap: 1.5
          }
        }
      },
      diet_infos: [
        {
          icon: "/static/fried_egg.png",
          type: "早餐",
          diet_num: 2,
          diet_kcal: 316,
          diet_plan_kcal: 600
        },
        {
          icon: "/static/lunch.png",
          type: "午餐",
          diet_num: 2,
          diet_kcal: 316,
          diet_plan_kcal: 600
        },
        {
          icon: "/static/dinner.png",
          type: "晚饭",
          diet_num: 2,
          diet_kcal: 316,
          diet_plan_kcal: 600
        },
        {
          icon: "/static/extra_meal.png",
          type: "加餐",
          diet_num: 2,
          diet_kcal: 316,
          diet_plan_kcal: 600
        }
      ],
      // 运动统计数据
      exerciseStats: {
        totalCalories: 0,
        targetCalories: 500,
        totalDuration: 0,
        targetDuration: 60,
        totalSteps: 0,
        targetSteps: 1e4
      },
      userInfo: {
        dailyCalorieGoal: 600,
        dailyExerciseGoal: 30,
        dailyStepGoal: 1e4
      }
    };
  },
  onLoad() {
    this.loadUserInfo();
    this.loadDietRecords();
    this.loadExerciseData();
    common_vendor.index.$on("refreshExerciseData", () => {
      this.loadExerciseData();
    });
  },
  onShow() {
    this.loadExerciseData();
  },
  onUnload() {
    common_vendor.index.$off("refreshExerciseData");
  },
  methods: {
    // 加载用户信息
    loadUserInfo() {
      const token = common_vendor.index.getStorageSync("token");
      const storedUserInfo = common_vendor.index.getStorageSync("userInfo");
      if (storedUserInfo) {
        this.userInfo = {
          ...this.userInfo,
          ...storedUserInfo
        };
      }
      if (!token) {
        common_vendor.index.__f__("log", "at pages/index/index.vue:464", "未登录，使用默认用户信息");
        return;
      }
      common_vendor.index.request({
        url: "http://localhost:8088/api/users/me",
        method: "GET",
        header: {
          "Authorization": "Bearer " + token
        },
        success: (res) => {
          if (res.data.code === 0) {
            const userData = res.data.data;
            this.userInfo = {
              ...this.userInfo,
              ...userData
            };
            common_vendor.index.setStorageSync("userInfo", this.userInfo);
          }
        },
        fail: () => {
          common_vendor.index.__f__("log", "at pages/index/index.vue:509", "加载用户信息失败，使用本地数据");
        }
      });
    },
    // 加载饮食记录
    loadDietRecords() {
      const token = common_vendor.index.getStorageSync("token");
      if (!token) {
        common_vendor.index.showToast({
          title: "请先登录",
          icon: "none"
        });
        return;
      }
      common_vendor.index.request({
        url: "http://localhost:8088/api/diet-records",
        method: "GET",
        header: {
          "Authorization": "Bearer " + token
        },
        success: (res) => {
          if (res.data.code === 0) {
            const pageData = res.data.data;
            const records = pageData.records || [];
            this.diet_infos = records.map((record) => ({
              id: record.id,
              // 添加记录ID
              icon: this.getMealIcon(record.mealType),
              type: record.mealType,
              diet_num: 1,
              // 默认值，实际应该从record.items计算
              diet_kcal: record.planCalories || 0,
              diet_plan_kcal: record.planCalories || 600
            }));
          }
        },
        fail: () => {
          common_vendor.index.__f__("log", "at pages/index/index.vue:583", "加载饮食记录失败，使用本地数据");
        }
      });
    },
    // 加载运动数据
    loadExerciseData() {
      const token = common_vendor.index.getStorageSync("token");
      if (!token) {
        common_vendor.index.__f__("log", "at pages/index/index.vue:600", "未登录，跳过运动数据加载");
        return;
      }
      common_vendor.index.request({
        url: "http://localhost:8088/api/exercise-records",
        method: "GET",
        header: {
          "Authorization": "Bearer " + token
        },
        success: (res) => {
          if (res.data.code === 0) {
            const records = res.data.data.records || [];
            this.updateChartData(records);
          }
        },
        fail: () => {
          common_vendor.index.__f__("log", "at pages/index/index.vue:633", "加载运动数据失败，使用默认数据");
        }
      });
    },
    // 更新图表数据
    updateChartData(exerciseRecords) {
      const today = (/* @__PURE__ */ new Date()).toISOString().split("T")[0];
      const todayRecords = exerciseRecords.filter((record) => {
        const recordDate = new Date(record.exerciseTime).toISOString().split("T")[0];
        return recordDate === today;
      });
      let totalDuration = 0;
      let totalCalories = 0;
      let totalSteps = 0;
      todayRecords.forEach((record) => {
        totalDuration += record.duration || 0;
        totalCalories += record.calories || 0;
        totalSteps += this.estimateSteps(record.exerciseType || record.exerciseName, record.duration || 0);
      });
      const targetSteps = this.userInfo.dailyStepGoal || 1e4;
      const targetDuration = this.userInfo.dailyExerciseGoal || 30;
      const targetCalories = this.userInfo.dailyCalorieGoal || 600;
      const stepsProgress = Math.min(totalSteps / targetSteps, 1);
      const durationProgress = Math.min(totalDuration / targetDuration, 1);
      const caloriesProgress = Math.min(totalCalories / targetCalories, 1);
      this.chartData = {
        series: [
          {
            name: "每日步数",
            data: stepsProgress
          },
          {
            name: "锻炼时长",
            data: durationProgress
          },
          {
            name: "活动热量",
            data: caloriesProgress
          }
        ]
      };
      this.exerciseStats = {
        totalCalories: Math.round(totalCalories),
        targetCalories,
        totalDuration: Math.round(totalDuration),
        targetDuration,
        totalSteps: Math.round(totalSteps),
        targetSteps
      };
      common_vendor.index.__f__("log", "at pages/index/index.vue:754", "图表数据已更新:", {
        totalSteps,
        totalDuration,
        totalCalories,
        stepsProgress: stepsProgress.toFixed(2),
        durationProgress: durationProgress.toFixed(2),
        caloriesProgress: caloriesProgress.toFixed(2)
      });
    },
    // 根据运动类型和时长估算步数
    estimateSteps(exerciseType, duration) {
      const stepsPerMinute = {
        "跑步": 150,
        // 跑步每分钟约150步
        "游泳": 0,
        // 游泳不计步数
        "骑行": 0,
        // 骑行不计步数
        "健身": 20,
        // 健身每分钟约20步
        "瑜伽": 10,
        // 瑜伽每分钟约10步
        "篮球": 120,
        // 篮球每分钟约120步
        "足球": 130,
        // 足球每分钟约130步
        "羽毛球": 80,
        // 羽毛球每分钟约80步
        "乒乓球": 30,
        // 乒乓球每分钟约30步
        "其他": 50
        // 其他运动每分钟约50步
      };
      const stepsPerMin = stepsPerMinute[exerciseType] || 50;
      return Math.round(stepsPerMin * duration);
    },
    // 获取餐别图标
    getMealIcon(mealType) {
      const iconMap = {
        "早餐": "/static/fried_egg.png",
        "午餐": "/static/lunch.png",
        "晚饭": "/static/dinner.png",
        "加餐": "/static/extra_meal.png"
      };
      return iconMap[mealType] || "/static/fried_egg.png";
    },
    // 刷新运动数据（供其他页面调用）
    refreshExerciseData() {
      this.loadExerciseData();
    },
    openDietModal() {
      this.showDietModal = true;
    },
    closeDietModal() {
      this.showDietModal = false;
    },
    onMealTypeChange(e) {
      const index = e.detail.value;
      this.dietForm.type = this.mealTypes[index];
      const iconMap = {
        "早餐": "/static/fried_egg.png",
        "午餐": "/static/lunch.png",
        "晚饭": "/static/dinner.png",
        "加餐": "/static/extra_meal.png"
      };
      this.dietForm.icon = iconMap[this.dietForm.type] || "/static/fried_egg.png";
    },
    goSelectFood() {
      common_vendor.index.navigateTo({
        url: "/pages/FoodSearch/FoodSearch",
        events: {
          // none
        },
        success: (res) => {
          res.eventChannel.on("foodSelected", (food) => {
            this.dietForm.food_name = food.name;
            this.dietForm.diet_kcal = food.calories_per_100;
            common_vendor.index.showToast({
              title: "已选择：" + food.name,
              icon: "none"
            });
          });
        }
      });
    },
    goToRecipes() {
      common_vendor.index.navigateTo({
        url: "/pages/Recipes/Recipes"
      });
    },
    openDietDetail(di) {
      if (di.id) {
        common_vendor.index.navigateTo({
          url: `/pages/DietRecordDetail/DietRecordDetail?recordId=${di.id}`
        });
      } else {
        common_vendor.index.navigateTo({
          url: "/pages/DietRecordDetail/DietRecordDetail"
        });
      }
    },
    submitDietRecord() {
      const n = Number(this.dietForm.diet_num);
      const kcal = Number(this.dietForm.diet_kcal);
      const plan = Number(this.dietForm.diet_plan_kcal);
      if (!this.dietForm.type || !n || n <= 0 || isNaN(kcal) || kcal <= 0) {
        common_vendor.index.showToast({
          title: "请填写有效信息",
          icon: "none"
        });
        return;
      }
      const dietRecord = {
        mealType: this.dietForm.type,
        recordedAt: (/* @__PURE__ */ new Date()).toISOString(),
        planCalories: plan > 0 ? plan : 600,
        note: ""
      };
      common_vendor.index.__f__("log", "at pages/index/index.vue:981", "发送的饮食记录数据:", JSON.stringify(dietRecord, null, 2));
      common_vendor.index.__f__("log", "at pages/index/index.vue:983", "dietForm数据:", JSON.stringify(this.dietForm, null, 2));
      const token = common_vendor.index.getStorageSync("token");
      common_vendor.index.request({
        url: "http://localhost:8088/api/diet-records",
        method: "POST",
        data: dietRecord,
        header: {
          "Authorization": token ? "Bearer " + token : ""
        },
        success: (res) => {
          if (res.data.code === 0) {
            this.diet_infos.unshift({
              icon: this.dietForm.icon,
              type: this.dietForm.type,
              diet_num: n,
              diet_kcal: kcal,
              diet_plan_kcal: plan > 0 ? plan : 600
            });
            this.closeDietModal();
            common_vendor.index.showToast({
              title: "已记录",
              icon: "success"
            });
          } else {
            common_vendor.index.showToast({
              title: "保存失败",
              icon: "none"
            });
          }
        },
        fail: () => {
          this.diet_infos.unshift({
            icon: this.dietForm.icon,
            type: this.dietForm.type,
            diet_num: n,
            diet_kcal: kcal,
            diet_plan_kcal: plan > 0 ? plan : 600
          });
          this.closeDietModal();
          common_vendor.index.showToast({
            title: "已记录(离线)",
            icon: "success"
          });
        }
      });
    }
  }
};
if (!Array) {
  const _easycom_qiun_data_charts2 = common_vendor.resolveComponent("qiun-data-charts");
  _easycom_qiun_data_charts2();
}
const _easycom_qiun_data_charts = () => "../../uni_modules/qiun-data-charts/components/qiun-data-charts/qiun-data-charts.js";
if (!Math) {
  _easycom_qiun_data_charts();
}
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.p({
      type: "arcbar",
      opts: $data.opts,
      chartData: $data.chartData
    }),
    b: common_vendor.t($data.exerciseStats.totalCalories),
    c: common_vendor.t($data.exerciseStats.targetCalories),
    d: common_vendor.t($data.exerciseStats.totalDuration),
    e: common_vendor.t($data.exerciseStats.targetDuration),
    f: common_vendor.t($data.exerciseStats.totalSteps),
    g: common_vendor.t($data.exerciseStats.targetSteps),
    h: common_vendor.o((...args) => $options.openDietModal && $options.openDietModal(...args)),
    i: common_vendor.o((...args) => $options.goToRecipes && $options.goToRecipes(...args)),
    j: common_vendor.f($data.diet_infos, (di, k0, i0) => {
      return {
        a: di.icon,
        b: common_vendor.t(di.type),
        c: common_vendor.t(di.diet_num),
        d: common_vendor.t(di.diet_kcal),
        e: common_vendor.t(di.diet_plan_kcal),
        f: di.type,
        g: common_vendor.o(($event) => $options.openDietDetail(di), di.type)
      };
    }),
    k: $data.showDietModal
  }, $data.showDietModal ? {
    l: common_vendor.o((...args) => $options.closeDietModal && $options.closeDietModal(...args))
  } : {}, {
    m: $data.showDietModal
  }, $data.showDietModal ? common_vendor.e({
    n: common_vendor.t($data.dietForm.type),
    o: $data.mealTypes,
    p: common_vendor.o((...args) => $options.onMealTypeChange && $options.onMealTypeChange(...args)),
    q: common_vendor.o((...args) => $options.goSelectFood && $options.goSelectFood(...args)),
    r: $data.dietForm.food_name
  }, $data.dietForm.food_name ? {
    s: common_vendor.t($data.dietForm.food_name)
  } : {}, {
    t: $data.dietForm.diet_num,
    v: common_vendor.o(common_vendor.m(($event) => $data.dietForm.diet_num = $event.detail.value, {
      number: true
    })),
    w: $data.dietForm.diet_kcal,
    x: common_vendor.o(common_vendor.m(($event) => $data.dietForm.diet_kcal = $event.detail.value, {
      number: true
    })),
    y: $data.dietForm.diet_plan_kcal,
    z: common_vendor.o(common_vendor.m(($event) => $data.dietForm.diet_plan_kcal = $event.detail.value, {
      number: true
    })),
    A: common_vendor.o((...args) => $options.closeDietModal && $options.closeDietModal(...args)),
    B: common_vendor.o((...args) => $options.submitDietRecord && $options.submitDietRecord(...args))
  }) : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-1cf27b2a"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/index/index.js.map
