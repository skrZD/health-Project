"use strict";
const common_vendor = require("../../common/vendor.js");
const _sfc_main = {
  data() {
    return {
      userInfo: {
        avatar: "/static/default_avatar.png",
        name: "用户",
        id: "",
        level: "健身新手",
        totalDays: 0,
        totalCalories: 0,
        totalMinutes: 0,
        age: 25,
        height: 170,
        weight: 65,
        gender: "男",
        dailyCalorieGoal: 600,
        dailyExerciseGoal: 30,
        dailyStepGoal: 1e4
      },
      appSettings: {
        notifications: true,
        privacy: true
      },
      isLoading: false
    };
  },
  onShow() {
    this.loadUserInfo();
    this.loadUserStats();
  },
  methods: {
    // 加载用户信息
    loadUserInfo() {
      const token = common_vendor.index.getStorageSync("token");
      const storedUserInfo = common_vendor.index.getStorageSync("userInfo");
      if (storedUserInfo) {
        this.userInfo = { ...this.userInfo, ...storedUserInfo };
      }
      if (!token) {
        common_vendor.index.showToast({
          title: "请先登录",
          icon: "none"
        });
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
              ...userData,
              avatar: userData.avatar || "/static/default_avatar.png"
            };
            common_vendor.index.setStorageSync("userInfo", this.userInfo);
          }
        },
        fail: () => {
          common_vendor.index.__f__("log", "at pages/userInfo/userInfo.vue:277", "加载用户信息失败，使用本地数据");
        }
      });
    },
    // 加载用户统计数据
    loadUserStats() {
      const token = common_vendor.index.getStorageSync("token");
      if (!token)
        return;
      common_vendor.index.request({
        url: "http://localhost:8088/api/exercise-records/stats",
        method: "GET",
        header: {
          "Authorization": "Bearer " + token
        },
        success: (res) => {
          if (res.data.code === 0) {
            const stats = res.data.data;
            this.userInfo.totalCalories = stats.totalCalories || 0;
            this.userInfo.totalMinutes = stats.totalMinutes || 0;
            this.userInfo.totalDays = stats.totalDays || 0;
          }
        },
        fail: () => {
          common_vendor.index.__f__("log", "at pages/userInfo/userInfo.vue:303", "加载运动统计失败");
        }
      });
    },
    // 上传头像
    uploadAvatar(filePath) {
      const token = common_vendor.index.getStorageSync("token");
      if (!token) {
        common_vendor.index.showToast({
          title: "请先登录",
          icon: "none"
        });
        return;
      }
      common_vendor.index.uploadFile({
        url: "http://localhost:8088/api/users/avatar",
        filePath,
        name: "avatar",
        header: {
          "Authorization": "Bearer " + token
        },
        success: (res) => {
          const data = JSON.parse(res.data);
          if (data.code === 0) {
            this.userInfo.avatar = data.data.avatarUrl;
            common_vendor.index.setStorageSync("userInfo", this.userInfo);
            common_vendor.index.showToast({
              title: "头像上传成功",
              icon: "success"
            });
          } else {
            common_vendor.index.showToast({
              title: "头像上传失败",
              icon: "none"
            });
          }
        },
        fail: () => {
          common_vendor.index.showToast({
            title: "头像上传失败",
            icon: "none"
          });
        }
      });
    },
    changeAvatar() {
      common_vendor.index.chooseImage({
        count: 1,
        sizeType: ["compressed"],
        sourceType: ["album", "camera"],
        success: (res) => {
          const tempFilePath = res.tempFilePaths[0];
          this.userInfo.avatar = tempFilePath;
          this.uploadAvatar(tempFilePath);
        },
        fail: () => {
          common_vendor.index.showToast({
            title: "选择图片失败",
            icon: "none"
          });
        }
      });
    },
    editInfo(type) {
      const currentValue = this.userInfo[type];
      const placeholder = type === "age" || type === "height" || type === "weight" ? `当前值: ${currentValue}` : `请输入${this.getFieldName(type)}`;
      common_vendor.index.showModal({
        title: "编辑" + this.getFieldName(type),
        editable: true,
        placeholderText: placeholder,
        success: (res) => {
          if (res.confirm && res.content) {
            let newValue = res.content;
            if (type === "age" || type === "height" || type === "weight") {
              newValue = parseFloat(newValue);
              if (isNaN(newValue)) {
                common_vendor.index.showToast({
                  title: "请输入有效数字",
                  icon: "none"
                });
                return;
              }
            }
            this.userInfo[type] = newValue;
            this.saveUserInfo();
            common_vendor.index.showToast({
              title: "更新成功",
              icon: "success"
            });
          }
        }
      });
    },
    editGoal(type) {
      const goalNames = {
        calorie: "每日热量目标",
        exercise: "每日运动时长",
        steps: "每日步数目标"
      };
      const currentValue = type === "calorie" ? this.userInfo.dailyCalorieGoal : type === "exercise" ? this.userInfo.dailyExerciseGoal : this.userInfo.dailyStepGoal;
      common_vendor.index.showModal({
        title: "编辑" + goalNames[type],
        editable: true,
        placeholderText: `当前值: ${currentValue}`,
        success: (res) => {
          if (res.confirm && res.content) {
            const value = parseInt(res.content);
            if (!isNaN(value) && value > 0) {
              if (type === "calorie") {
                this.userInfo.dailyCalorieGoal = value;
              } else if (type === "exercise") {
                this.userInfo.dailyExerciseGoal = value;
              } else if (type === "steps") {
                this.userInfo.dailyStepGoal = value;
              }
              this.saveUserGoals();
              common_vendor.index.showToast({
                title: "目标更新成功",
                icon: "success"
              });
            } else {
              common_vendor.index.showToast({
                title: "请输入有效数字",
                icon: "none"
              });
            }
          }
        }
      });
    },
    // 保存用户信息到后端
    saveUserInfo() {
      const token = common_vendor.index.getStorageSync("token");
      if (!token)
        return;
      const userData = {
        name: this.userInfo.name,
        age: this.userInfo.age,
        height: this.userInfo.height,
        weight: this.userInfo.weight,
        gender: this.userInfo.gender
      };
      common_vendor.index.request({
        url: "http://localhost:8088/api/users/me",
        method: "PUT",
        data: userData,
        header: {
          "Authorization": "Bearer " + token,
          "Content-Type": "application/json"
        },
        success: (res) => {
          if (res.data.code === 0) {
            common_vendor.index.setStorageSync("userInfo", this.userInfo);
            common_vendor.index.__f__("log", "at pages/userInfo/userInfo.vue:480", "用户信息保存成功");
          } else {
            common_vendor.index.__f__("log", "at pages/userInfo/userInfo.vue:482", "用户信息保存失败:", res.data.message);
          }
        },
        fail: () => {
          common_vendor.index.__f__("log", "at pages/userInfo/userInfo.vue:486", "用户信息保存失败");
        }
      });
    },
    // 保存用户目标到后端
    saveUserGoals() {
      const token = common_vendor.index.getStorageSync("token");
      if (!token)
        return;
      const goals = {
        dailyCalorieGoal: this.userInfo.dailyCalorieGoal,
        dailyExerciseGoal: this.userInfo.dailyExerciseGoal,
        dailyStepGoal: this.userInfo.dailyStepGoal
      };
      common_vendor.index.request({
        url: "http://localhost:8088/api/users/goals",
        method: "PUT",
        data: goals,
        header: {
          "Authorization": "Bearer " + token,
          "Content-Type": "application/json"
        },
        success: (res) => {
          if (res.data.code === 0) {
            common_vendor.index.setStorageSync("userInfo", this.userInfo);
            common_vendor.index.__f__("log", "at pages/userInfo/userInfo.vue:514", "用户目标保存成功");
          } else {
            common_vendor.index.__f__("log", "at pages/userInfo/userInfo.vue:516", "用户目标保存失败:", res.data.message);
          }
        },
        fail: () => {
          common_vendor.index.__f__("log", "at pages/userInfo/userInfo.vue:520", "用户目标保存失败");
        }
      });
    },
    getFieldName(type) {
      const names = {
        name: "姓名",
        age: "年龄",
        height: "身高",
        weight: "体重",
        gender: "性别"
      };
      return names[type] || "";
    },
    toggleSetting(type) {
      this.appSettings[type] = !this.appSettings[type];
    },
    onNotificationChange(e) {
      this.appSettings.notifications = e.detail.value;
      common_vendor.index.showToast({
        title: this.appSettings.notifications ? "通知已开启" : "通知已关闭",
        icon: "none"
      });
    },
    onPrivacyChange(e) {
      this.appSettings.privacy = e.detail.value;
      common_vendor.index.showToast({
        title: this.appSettings.privacy ? "隐私保护已开启" : "隐私保护已关闭",
        icon: "none"
      });
    },
    goToPage(page) {
      if (page === "about") {
        common_vendor.index.navigateTo({
          url: "/pages/about/about"
        });
      } else if (page === "feedback") {
        common_vendor.index.navigateTo({
          url: "/pages/feedback/feedback"
        });
      }
    },
    goToHealthProfile() {
      common_vendor.index.navigateTo({
        url: "/pages/HealthProfile/HealthProfile"
      });
    },
    logout() {
      common_vendor.index.showModal({
        title: "确认退出",
        content: "确定要退出登录吗？",
        success: (res) => {
          if (res.confirm) {
            common_vendor.index.removeStorageSync("token");
            common_vendor.index.removeStorageSync("userInfo");
            common_vendor.index.reLaunch({
              url: "/pages/login/login"
            });
            common_vendor.index.showToast({
              title: "已退出登录",
              icon: "success"
            });
          }
        }
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {
    a: $data.userInfo.avatar,
    b: common_vendor.o((...args) => $options.changeAvatar && $options.changeAvatar(...args)),
    c: common_vendor.t($data.userInfo.name),
    d: common_vendor.t($data.userInfo.id),
    e: common_vendor.t($data.userInfo.level),
    f: common_vendor.t($data.userInfo.totalDays),
    g: common_vendor.t($data.userInfo.totalCalories),
    h: common_vendor.t($data.userInfo.totalMinutes),
    i: common_vendor.t($data.userInfo.name),
    j: common_vendor.o(($event) => $options.editInfo("name")),
    k: common_vendor.t($data.userInfo.age),
    l: common_vendor.o(($event) => $options.editInfo("age")),
    m: common_vendor.t($data.userInfo.height),
    n: common_vendor.o(($event) => $options.editInfo("height")),
    o: common_vendor.t($data.userInfo.weight),
    p: common_vendor.o(($event) => $options.editInfo("weight")),
    q: common_vendor.t($data.userInfo.gender),
    r: common_vendor.o(($event) => $options.editInfo("gender")),
    s: common_vendor.o((...args) => $options.goToHealthProfile && $options.goToHealthProfile(...args)),
    t: common_vendor.t($data.userInfo.dailyCalorieGoal),
    v: common_vendor.o(($event) => $options.editGoal("calorie")),
    w: common_vendor.t($data.userInfo.dailyExerciseGoal),
    x: common_vendor.o(($event) => $options.editGoal("exercise")),
    y: common_vendor.t($data.userInfo.dailyStepGoal),
    z: common_vendor.o(($event) => $options.editGoal("steps")),
    A: $data.appSettings.notifications,
    B: common_vendor.o((...args) => $options.onNotificationChange && $options.onNotificationChange(...args)),
    C: common_vendor.o(($event) => $options.toggleSetting("notifications")),
    D: $data.appSettings.privacy,
    E: common_vendor.o((...args) => $options.onPrivacyChange && $options.onPrivacyChange(...args)),
    F: common_vendor.o(($event) => $options.toggleSetting("privacy")),
    G: common_vendor.o(($event) => $options.goToPage("about")),
    H: common_vendor.o(($event) => $options.goToPage("feedback")),
    I: common_vendor.o((...args) => $options.logout && $options.logout(...args))
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-875c692e"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/userInfo/userInfo.js.map
