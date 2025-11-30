"use strict";
const common_vendor = require("../../common/vendor.js");
const api_config = require("../../api/config.js");
const _sfc_main = {
  data() {
    return {
      profile: {
        height: null,
        weight: null,
        bmi: null,
        bloodPressureSystolic: null,
        bloodPressureDiastolic: null,
        bloodSugar: null,
        cholesterol: null,
        medicalHistory: "",
        allergies: "",
        medications: "",
        emergencyContact: "",
        emergencyPhone: ""
      }
    };
  },
  onLoad() {
    this.loadProfile();
  },
  methods: {
    // 加载健康档案
    loadProfile() {
      const token = common_vendor.index.getStorageSync("token");
      if (!token) {
        common_vendor.index.showToast({
          title: "请先登录",
          icon: "none"
        });
        return;
      }
      common_vendor.index.request({
        url: api_config.apiConfig.API_CONFIG.BASE_URL + api_config.apiConfig.API_CONFIG.HEALTH_PROFILES.ME,
        method: "GET",
        header: {
          "Authorization": "Bearer " + token
        },
        success: (res) => {
          if (res.data.code === 0 && res.data.data) {
            this.profile = { ...this.profile, ...res.data.data };
          }
        },
        fail: () => {
          common_vendor.index.__f__("log", "at pages/HealthProfile/HealthProfile.vue:183", "加载健康档案失败");
        }
      });
    },
    // 计算BMI
    calculateBMI() {
      if (this.profile.height && this.profile.weight && this.profile.height > 0) {
        const heightInMeters = this.profile.height / 100;
        this.profile.bmi = Math.round(this.profile.weight / (heightInMeters * heightInMeters) * 100) / 100;
      }
    },
    // 获取BMI分类
    getBmiCategory(bmi) {
      if (bmi < 18.5)
        return "偏瘦";
      if (bmi < 24)
        return "正常";
      if (bmi < 28)
        return "超重";
      return "肥胖";
    },
    // 获取BMI分类样式
    getBmiCategoryClass(bmi) {
      if (bmi < 18.5)
        return "bmi-underweight";
      if (bmi < 24)
        return "bmi-normal";
      if (bmi < 28)
        return "bmi-overweight";
      return "bmi-obese";
    },
    // 保存健康档案
    saveProfile() {
      const token = common_vendor.index.getStorageSync("token");
      if (!token) {
        common_vendor.index.showToast({
          title: "请先登录",
          icon: "none"
        });
        return;
      }
      common_vendor.index.request({
        url: api_config.apiConfig.API_CONFIG.BASE_URL + api_config.apiConfig.API_CONFIG.HEALTH_PROFILES.UPDATE,
        method: "POST",
        data: this.profile,
        header: {
          "Authorization": "Bearer " + token
        },
        success: (res) => {
          if (res.data.code === 0) {
            common_vendor.index.showToast({
              title: "保存成功",
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
          common_vendor.index.showToast({
            title: "网络错误",
            icon: "none"
          });
        }
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.o((...args) => $options.calculateBMI && $options.calculateBMI(...args)),
    b: $data.profile.height,
    c: common_vendor.o(common_vendor.m(($event) => $data.profile.height = $event.detail.value, {
      number: true
    })),
    d: common_vendor.o((...args) => $options.calculateBMI && $options.calculateBMI(...args)),
    e: $data.profile.weight,
    f: common_vendor.o(common_vendor.m(($event) => $data.profile.weight = $event.detail.value, {
      number: true
    })),
    g: $data.profile.bmi
  }, $data.profile.bmi ? {
    h: common_vendor.t($data.profile.bmi),
    i: common_vendor.t($options.getBmiCategory($data.profile.bmi)),
    j: common_vendor.n($options.getBmiCategoryClass($data.profile.bmi))
  } : {}, {
    k: $data.profile.bloodPressureSystolic,
    l: common_vendor.o(common_vendor.m(($event) => $data.profile.bloodPressureSystolic = $event.detail.value, {
      number: true
    })),
    m: $data.profile.bloodPressureDiastolic,
    n: common_vendor.o(common_vendor.m(($event) => $data.profile.bloodPressureDiastolic = $event.detail.value, {
      number: true
    })),
    o: $data.profile.bloodSugar,
    p: common_vendor.o(common_vendor.m(($event) => $data.profile.bloodSugar = $event.detail.value, {
      number: true
    })),
    q: $data.profile.cholesterol,
    r: common_vendor.o(common_vendor.m(($event) => $data.profile.cholesterol = $event.detail.value, {
      number: true
    })),
    s: $data.profile.medicalHistory,
    t: common_vendor.o(($event) => $data.profile.medicalHistory = $event.detail.value),
    v: $data.profile.allergies,
    w: common_vendor.o(($event) => $data.profile.allergies = $event.detail.value),
    x: $data.profile.medications,
    y: common_vendor.o(($event) => $data.profile.medications = $event.detail.value),
    z: $data.profile.emergencyContact,
    A: common_vendor.o(($event) => $data.profile.emergencyContact = $event.detail.value),
    B: $data.profile.emergencyPhone,
    C: common_vendor.o(($event) => $data.profile.emergencyPhone = $event.detail.value),
    D: common_vendor.o((...args) => $options.saveProfile && $options.saveProfile(...args))
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-98e5f5ae"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/HealthProfile/HealthProfile.js.map
