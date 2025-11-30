"use strict";
const common_vendor = require("../../common/vendor.js");
const api_config = require("../../api/config.js");
const common_assets = require("../../common/assets.js");
const _sfc_main = {
  data() {
    return {
      isLoading: false
    };
  },
  onLoad() {
    this.checkLoginStatus();
  },
  methods: {
    // 检查登录状态
    checkLoginStatus() {
      const token = common_vendor.index.getStorageSync("token");
      const userInfo = common_vendor.index.getStorageSync("userInfo");
      if (token && userInfo) {
        this.validateToken(token);
      }
    },
    // 验证token有效性
    validateToken(token) {
      common_vendor.index.request({
        url: api_config.apiConfig.API_CONFIG.BASE_URL + api_config.apiConfig.API_CONFIG.AUTH.ME,
        method: "GET",
        header: {
          "Authorization": "Bearer " + token
        },
        success: (res) => {
          if (res.data.code === 0) {
            common_vendor.index.switchTab({
              url: "/pages/index/index"
            });
          } else {
            this.clearLoginData();
          }
        },
        fail: () => {
          this.clearLoginData();
        }
      });
    },
    // 微信登录
    handleWechatLogin() {
      if (this.isLoading)
        return;
      this.isLoading = true;
      common_vendor.index.login({
        provider: "weixin",
        success: (loginRes) => {
          if (loginRes.code) {
            this.loginWithCode(loginRes.code);
          } else {
            this.isLoading = false;
            common_vendor.index.showToast({
              title: "获取登录凭证失败",
              icon: "none"
            });
          }
        },
        fail: (err) => {
          this.isLoading = false;
          common_vendor.index.__f__("error", "at pages/login/login.vue:108", "微信登录失败:", err);
          common_vendor.index.showToast({
            title: "微信登录失败",
            icon: "none"
          });
        }
      });
    },
    // 使用code登录
    loginWithCode(code) {
      common_vendor.index.request({
        url: api_config.apiConfig.API_CONFIG.BASE_URL + api_config.apiConfig.API_CONFIG.AUTH.LOGIN,
        method: "POST",
        data: {
          code
        },
        success: (res) => {
          this.isLoading = false;
          if (res.data.code === 0) {
            const { token, user } = res.data.data;
            common_vendor.index.setStorageSync("token", token);
            common_vendor.index.setStorageSync("userInfo", user);
            common_vendor.index.showToast({
              title: "登录成功",
              icon: "success"
            });
            setTimeout(() => {
              common_vendor.index.switchTab({
                url: "/pages/index/index"
              });
            }, 1500);
          } else {
            common_vendor.index.showToast({
              title: res.data.message || "登录失败",
              icon: "none"
            });
          }
        },
        fail: (err) => {
          this.isLoading = false;
          common_vendor.index.__f__("error", "at pages/login/login.vue:155", "登录请求失败:", err);
          common_vendor.index.showToast({
            title: "网络错误，请重试",
            icon: "none"
          });
        }
      });
    },
    // 清除登录数据
    clearLoginData() {
      common_vendor.index.removeStorageSync("token");
      common_vendor.index.removeStorageSync("userInfo");
    },
    // 显示隐私政策
    showPrivacy() {
      common_vendor.index.showModal({
        title: "隐私政策",
        content: "我们重视您的隐私保护...",
        showCancel: false
      });
    },
    // 显示用户协议
    showTerms() {
      common_vendor.index.showModal({
        title: "用户协议",
        content: "欢迎使用健康管理应用...",
        showCancel: false
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {
    a: common_assets._imports_0,
    b: common_vendor.t($data.isLoading ? "登录中..." : "微信授权登录"),
    c: common_vendor.o((...args) => $options.handleWechatLogin && $options.handleWechatLogin(...args)),
    d: $data.isLoading,
    e: $data.isLoading,
    f: common_vendor.o((...args) => $options.showPrivacy && $options.showPrivacy(...args)),
    g: common_vendor.o((...args) => $options.showTerms && $options.showTerms(...args))
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-e4e4508d"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/login/login.js.map
