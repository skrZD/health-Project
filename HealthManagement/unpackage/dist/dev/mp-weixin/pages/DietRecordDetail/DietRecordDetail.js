"use strict";
const common_vendor = require("../../common/vendor.js");
const _sfc_main = {
  data() {
    return {
      record: {
        id: null,
        mealType: "早餐",
        recordedAt: "",
        planCalories: 600,
        note: ""
      },
      items: [],
      totalKcal: 0,
      mealTypes: ["早餐", "午餐", "晚饭", "加餐"],
      mealTypeIndex: 0
    };
  },
  onLoad(query) {
    common_vendor.index.__f__("log", "at pages/DietRecordDetail/DietRecordDetail.vue:71", "饮食记录详情页面加载，参数:", query);
    if (query.recordId) {
      common_vendor.index.__f__("log", "at pages/DietRecordDetail/DietRecordDetail.vue:74", "加载饮食记录详情，recordId:", query.recordId);
      this.loadDietRecord(query.recordId);
    } else if (query.recipeId) {
      common_vendor.index.__f__("log", "at pages/DietRecordDetail/DietRecordDetail.vue:78", "加载食谱数据，recipeId:", query.recipeId);
      this.loadRecipeData(query.recipeId, query.recipeName);
    } else {
      common_vendor.index.__f__("log", "at pages/DietRecordDetail/DietRecordDetail.vue:82", "没有参数，显示新建页面");
      this.record.recordedAt = this.getNow();
    }
  },
  methods: {
    getNow() {
      const d = /* @__PURE__ */ new Date();
      const p = (n) => String(n).padStart(2, "0");
      return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())}T${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}.000Z`;
    },
    formatDateTime(dateTimeStr) {
      if (!dateTimeStr)
        return "";
      const date = new Date(dateTimeStr);
      const p = (n) => String(n).padStart(2, "0");
      return `${date.getFullYear()}-${p(date.getMonth() + 1)}-${p(date.getDate())} ${p(date.getHours())}:${p(date.getMinutes())}`;
    },
    // 加载食谱数据
    loadRecipeData(recipeId, recipeName) {
      this.record.recordedAt = this.getNow();
      this.record.note = `使用食谱：${decodeURIComponent(recipeName || "")}`;
      common_vendor.index.request({
        url: `http://localhost:8088/api/recipes/${recipeId}/detail`,
        method: "GET",
        success: (res) => {
          if (res.data.code === 0) {
            const data = res.data.data;
            this.record.note = `使用食谱：${data.recipe.name}`;
            this.items = data.items.map((item) => ({
              id: item.id,
              foodId: item.foodId,
              recipeId: null,
              // 食谱明细不关联食谱ID
              foodName: item.foodName,
              quantity: item.quantity,
              unit: item.quantityUnit,
              calories: 0
              // 初始为0，稍后计算
            }));
            this.calculateItemCalories();
          } else {
            common_vendor.index.showToast({
              title: "加载食谱失败",
              icon: "none"
            });
          }
        },
        fail: () => {
          common_vendor.index.showToast({
            title: "网络错误",
            icon: "none"
          });
        }
      });
    },
    loadDietRecord(recordId) {
      common_vendor.index.__f__("log", "at pages/DietRecordDetail/DietRecordDetail.vue:143", "开始加载饮食记录详情，recordId:", recordId);
      const token = common_vendor.index.getStorageSync("token");
      if (!token) {
        common_vendor.index.showToast({ title: "请先登录", icon: "none" });
        return;
      }
      common_vendor.index.request({
        url: `http://localhost:8088/api/diet-records/${recordId}`,
        method: "GET",
        header: {
          "Authorization": "Bearer " + token
        },
        success: (res) => {
          common_vendor.index.__f__("log", "at pages/DietRecordDetail/DietRecordDetail.vue:158", "饮食记录详情API响应:", res);
          if (res.data.code === 0) {
            this.record = res.data.data;
            common_vendor.index.__f__("log", "at pages/DietRecordDetail/DietRecordDetail.vue:161", "设置记录数据:", this.record);
            this.loadDietRecordItems(recordId);
          } else {
            common_vendor.index.__f__("log", "at pages/DietRecordDetail/DietRecordDetail.vue:165", "获取记录失败:", res.data.message);
            common_vendor.index.showToast({ title: "获取记录失败", icon: "none" });
          }
        },
        fail: (err) => {
          common_vendor.index.__f__("log", "at pages/DietRecordDetail/DietRecordDetail.vue:170", "网络错误:", err);
          common_vendor.index.showToast({ title: "网络错误", icon: "none" });
        }
      });
    },
    loadDietRecordItems(recordId) {
      common_vendor.index.__f__("log", "at pages/DietRecordDetail/DietRecordDetail.vue:176", "开始加载饮食记录明细，recordId:", recordId);
      const token = common_vendor.index.getStorageSync("token");
      common_vendor.index.request({
        url: `http://localhost:8088/api/diet-records/${recordId}/items`,
        method: "GET",
        header: {
          "Authorization": "Bearer " + token
        },
        success: (res) => {
          common_vendor.index.__f__("log", "at pages/DietRecordDetail/DietRecordDetail.vue:187", "饮食记录明细API响应:", res);
          if (res.data.code === 0) {
            this.items = res.data.data || [];
            common_vendor.index.__f__("log", "at pages/DietRecordDetail/DietRecordDetail.vue:190", "设置明细数据:", this.items);
            this.recalcTotal();
          } else {
            common_vendor.index.__f__("log", "at pages/DietRecordDetail/DietRecordDetail.vue:193", "获取明细失败:", res.data.message);
          }
        },
        fail: (err) => {
          common_vendor.index.__f__("log", "at pages/DietRecordDetail/DietRecordDetail.vue:197", "获取明细网络错误:", err);
        }
      });
    },
    // 餐别选择
    onMealTypeChange(e) {
      this.mealTypeIndex = e.detail.value;
      this.record.mealType = this.mealTypes[e.detail.value];
    },
    // 计算食物热量
    calculateItemCalories() {
      this.items.forEach((item, index) => {
        this.calculateSingleItemCalories(item, index);
      });
    },
    // 计算单个食物项的热量
    calculateSingleItemCalories(item, index) {
      let caloriesPer100g = 0;
      const foodName = item.foodName.toLowerCase();
      if (foodName.includes("鸡胸肉") || foodName.includes("鸡肉")) {
        caloriesPer100g = 165;
      } else if (foodName.includes("牛肉")) {
        caloriesPer100g = 250;
      } else if (foodName.includes("三文鱼") || foodName.includes("鲈鱼")) {
        caloriesPer100g = 208;
      } else if (foodName.includes("鸡蛋")) {
        caloriesPer100g = 155;
      } else if (foodName.includes("牛奶")) {
        caloriesPer100g = 42;
      } else if (foodName.includes("燕麦")) {
        caloriesPer100g = 389;
      } else if (foodName.includes("米饭")) {
        caloriesPer100g = 130;
      } else if (foodName.includes("蔬菜") || foodName.includes("生菜") || foodName.includes("西兰花")) {
        caloriesPer100g = 25;
      } else if (foodName.includes("番茄")) {
        caloriesPer100g = 18;
      } else if (foodName.includes("豆腐")) {
        caloriesPer100g = 76;
      } else if (foodName.includes("酸奶")) {
        caloriesPer100g = 59;
      } else if (foodName.includes("水果") || foodName.includes("草莓") || foodName.includes("蓝莓")) {
        caloriesPer100g = 50;
      } else {
        caloriesPer100g = 100;
      }
      let quantity = parseFloat(item.quantity) || 0;
      let calories = caloriesPer100g * quantity / 100;
      this.$set(this.items, index, {
        ...item,
        calories: Math.round(calories)
      });
      this.recalcTotal();
    },
    recalcTotal() {
      this.totalKcal = this.items.reduce((s, i) => s + (Number(i.calories) || 0), 0);
    },
    // 保存饮食记录
    saveDietRecord() {
      const token = common_vendor.index.getStorageSync("token");
      if (!token) {
        common_vendor.index.showToast({ title: "请先登录", icon: "none" });
        return;
      }
      const dietRecord = {
        mealType: this.record.mealType,
        recordedAt: this.record.recordedAt,
        planCalories: this.record.planCalories,
        note: this.record.note,
        items: this.items.map((item) => ({
          foodId: item.foodId || null,
          // 食物ID，如果没有则为null
          recipeId: item.recipeId || null,
          // 食谱ID，如果没有则为null
          quantity: item.quantity,
          unit: item.unit,
          calories: item.calories
        }))
      };
      common_vendor.index.request({
        url: "http://localhost:8088/api/diet-records",
        method: "POST",
        header: {
          "Authorization": "Bearer " + token,
          "Content-Type": "application/json"
        },
        data: dietRecord,
        success: (res) => {
          if (res.data.code === 0) {
            common_vendor.index.showToast({
              title: "保存成功",
              icon: "success"
            });
            setTimeout(() => {
              common_vendor.index.navigateBack();
            }, 1500);
          } else {
            common_vendor.index.showToast({
              title: res.data.message || "保存失败",
              icon: "none"
            });
          }
        },
        fail: () => {
          common_vendor.index.showToast({
            title: "网络错误",
            icon: "none"
          });
        }
      });
    },
    goBack() {
      common_vendor.index.navigateBack();
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: !$data.record.id
  }, !$data.record.id ? {
    b: common_vendor.t($data.record.mealType),
    c: common_vendor.o((...args) => $options.onMealTypeChange && $options.onMealTypeChange(...args)),
    d: $data.mealTypeIndex,
    e: $data.mealTypes
  } : {
    f: common_vendor.t($data.record.mealType)
  }, {
    g: common_vendor.t($options.formatDateTime($data.record.recordedAt)),
    h: common_vendor.t($data.record.planCalories),
    i: common_vendor.t($data.record.note || "无"),
    j: common_vendor.f($data.items, (item, k0, i0) => {
      return {
        a: common_vendor.t(item.foodName),
        b: common_vendor.t(item.quantity),
        c: common_vendor.t(item.unit),
        d: common_vendor.t(item.calories),
        e: item.id
      };
    }),
    k: $data.items.length === 0
  }, $data.items.length === 0 ? {} : {}, {
    l: common_vendor.t($data.totalKcal),
    m: common_vendor.o((...args) => $options.goBack && $options.goBack(...args)),
    n: !$data.record.id
  }, !$data.record.id ? {
    o: common_vendor.o((...args) => $options.saveDietRecord && $options.saveDietRecord(...args))
  } : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-56e92e55"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/DietRecordDetail/DietRecordDetail.js.map
