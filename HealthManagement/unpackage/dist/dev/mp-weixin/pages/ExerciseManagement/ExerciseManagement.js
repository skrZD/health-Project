"use strict";
const common_vendor = require("../../common/vendor.js");
const _sfc_main = {
  data() {
    return {
      showAddModal: false,
      isEdit: false,
      editId: null,
      exerciseTypeIndex: 0,
      exerciseTypes: ["跑步", "游泳", "骑行", "健身", "瑜伽", "篮球", "足球", "羽毛球", "乒乓球", "其他"],
      formData: {
        name: "",
        duration: "",
        calories: "",
        time: "12:00",
        icon: ""
      },
      exerciseList: [
        {
          id: 1,
          name: "跑步",
          duration: 30,
          calories: 300,
          time: "08:30",
          icon: "🏃"
        },
        {
          id: 2,
          name: "游泳",
          duration: 45,
          calories: 450,
          time: "19:00",
          icon: "🏊"
        }
      ],
      todayStats: {
        duration: 75,
        calories: 750,
        count: 2
      },
      userInfo: {
        dailyCalorieGoal: 600,
        dailyExerciseGoal: 30
      }
    };
  },
  onLoad() {
    this.loadUserInfo();
    this.loadExerciseRecords();
    this.updateTodayStats();
  },
  methods: {
    onExerciseTypeChange(e) {
      this.exerciseTypeIndex = e.detail.value;
      this.formData.name = this.exerciseTypes[this.exerciseTypeIndex];
      this.formData.icon = this.getExerciseIcon(this.formData.name);
    },
    onTimeChange(e) {
      this.formData.time = e.detail.value;
    },
    getExerciseIcon(name) {
      const iconMap = {
        "跑步": "🏃",
        "游泳": "🏊",
        "骑行": "🚴",
        "健身": "🏋️",
        "瑜伽": "🧘",
        "篮球": "🏀",
        "足球": "⚽",
        "羽毛球": "🏸",
        "乒乓球": "🏓",
        "其他": "💪"
      };
      return iconMap[name] || "💪";
    },
    editExercise(exercise) {
      this.isEdit = true;
      this.editId = exercise.id;
      this.formData = { ...exercise };
      this.exerciseTypeIndex = this.exerciseTypes.indexOf(exercise.name);
      this.showAddModal = true;
    },
    deleteExercise(id) {
      common_vendor.index.showModal({
        title: "确认删除",
        content: "确定要删除这条运动记录吗？",
        success: (res) => {
          if (res.confirm) {
            this.exerciseList = this.exerciseList.filter((item) => item.id !== id);
            this.updateTodayStats();
          }
        }
      });
    },
    saveExercise() {
      if (!this.formData.duration || !this.formData.calories) {
        common_vendor.index.showToast({
          title: "请填写完整信息",
          icon: "none"
        });
        return;
      }
      if (!this.formData.name) {
        this.formData.name = this.exerciseTypes[this.exerciseTypeIndex];
        this.formData.icon = this.getExerciseIcon(this.formData.name);
      }
      const exerciseData = {
        exerciseType: this.formData.name,
        exerciseName: this.formData.name,
        duration: parseInt(this.formData.duration),
        calories: parseInt(this.formData.calories),
        exerciseTime: (/* @__PURE__ */ new Date()).toISOString().replace("Z", "Z"),
        note: ""
      };
      common_vendor.index.__f__("log", "at pages/ExerciseManagement/ExerciseManagement.vue:218", "发送的运动记录数据:", exerciseData);
      common_vendor.index.__f__("log", "at pages/ExerciseManagement/ExerciseManagement.vue:219", "formData:", this.formData);
      const url = this.isEdit ? `http://localhost:8088/api/exercise-records/${this.editId}` : "http://localhost:8088/api/exercise-records";
      const method = this.isEdit ? "PUT" : "POST";
      const token = common_vendor.index.getStorageSync("token");
      common_vendor.index.request({
        url,
        method,
        data: exerciseData,
        header: {
          "Authorization": token ? "Bearer " + token : "",
          "Content-Type": "application/json"
        },
        success: (res) => {
          if (res.data.code === 0) {
            if (this.isEdit) {
              const index = this.exerciseList.findIndex((item) => item.id === this.editId);
              if (index !== -1) {
                this.exerciseList[index] = { ...this.formData, id: this.editId };
              }
            } else {
              const newExercise = {
                ...this.formData,
                id: Date.now()
              };
              this.exerciseList.unshift(newExercise);
            }
            this.updateTodayStats();
            this.closeModal();
            common_vendor.index.showToast({ title: "保存成功", icon: "success" });
            common_vendor.index.$emit("refreshExerciseData");
          } else {
            common_vendor.index.showToast({ title: "保存失败", icon: "none" });
          }
        },
        fail: () => {
          if (this.isEdit) {
            const index = this.exerciseList.findIndex((item) => item.id === this.editId);
            if (index !== -1) {
              this.exerciseList[index] = { ...this.formData, id: this.editId };
            }
          } else {
            const newExercise = {
              ...this.formData,
              id: Date.now()
            };
            this.exerciseList.unshift(newExercise);
          }
          this.updateTodayStats();
          this.closeModal();
          common_vendor.index.showToast({ title: "保存成功(离线)", icon: "success" });
          common_vendor.index.$emit("refreshExerciseData");
        }
      });
    },
    updateTodayStats() {
      (/* @__PURE__ */ new Date()).toDateString();
      const todayExercises = this.exerciseList.filter((exercise) => {
        return true;
      });
      this.todayStats = {
        duration: todayExercises.reduce((sum, exercise) => sum + parseInt(exercise.duration), 0),
        calories: todayExercises.reduce((sum, exercise) => sum + parseInt(exercise.calories), 0),
        count: todayExercises.length
      };
    },
    closeModal() {
      this.showAddModal = false;
      this.isEdit = false;
      this.editId = null;
      this.formData = {
        name: "",
        duration: "",
        calories: "",
        time: "12:00",
        icon: ""
      };
      this.exerciseTypeIndex = 0;
    },
    // 加载用户信息
    loadUserInfo() {
      const token = common_vendor.index.getStorageSync("token");
      const storedUserInfo = common_vendor.index.getStorageSync("userInfo");
      if (storedUserInfo) {
        this.userInfo = { ...this.userInfo, ...storedUserInfo };
      }
      if (!token) {
        common_vendor.index.__f__("log", "at pages/ExerciseManagement/ExerciseManagement.vue:324", "未登录，使用默认用户信息");
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
          common_vendor.index.__f__("log", "at pages/ExerciseManagement/ExerciseManagement.vue:347", "加载用户信息失败，使用本地数据");
        }
      });
    },
    // 加载运动记录
    loadExerciseRecords() {
      const token = common_vendor.index.getStorageSync("token");
      if (!token) {
        common_vendor.index.showToast({
          title: "请先登录",
          icon: "none"
        });
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
            this.exerciseList = records.map((record) => ({
              id: record.id,
              name: record.exerciseName || record.exerciseType,
              duration: record.duration,
              calories: record.calories,
              time: this.formatTime(record.exerciseTime),
              icon: this.getExerciseIcon(record.exerciseType || record.exerciseName)
            }));
            this.updateTodayStats();
          }
        },
        fail: () => {
          common_vendor.index.__f__("log", "at pages/ExerciseManagement/ExerciseManagement.vue:385", "加载运动记录失败，使用本地数据");
        }
      });
    },
    // 格式化时间为HH:mm格式
    formatTime(dateTimeString) {
      if (!dateTimeString)
        return "00:00";
      try {
        const date = new Date(dateTimeString);
        const hours = date.getHours().toString().padStart(2, "0");
        const minutes = date.getMinutes().toString().padStart(2, "0");
        return `${hours}:${minutes}`;
      } catch (e) {
        common_vendor.index.__f__("error", "at pages/ExerciseManagement/ExerciseManagement.vue:399", "时间格式化错误:", e);
        return "00:00";
      }
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.o(($event) => $data.showAddModal = true),
    b: common_vendor.t($data.todayStats.duration),
    c: common_vendor.t($data.todayStats.calories),
    d: common_vendor.t($data.todayStats.count),
    e: common_vendor.f($data.exerciseList, (exercise, k0, i0) => {
      return {
        a: common_vendor.t(exercise.icon),
        b: common_vendor.t(exercise.name),
        c: common_vendor.t(exercise.duration),
        d: common_vendor.t(exercise.calories),
        e: common_vendor.t(exercise.time),
        f: common_vendor.o(($event) => $options.editExercise(exercise), exercise.id),
        g: common_vendor.o(($event) => $options.deleteExercise(exercise.id), exercise.id),
        h: exercise.id
      };
    }),
    f: $data.showAddModal
  }, $data.showAddModal ? {
    g: common_vendor.t($data.isEdit ? "编辑运动" : "添加运动"),
    h: common_vendor.o((...args) => $options.closeModal && $options.closeModal(...args)),
    i: common_vendor.t($data.exerciseTypes[$data.exerciseTypeIndex]),
    j: common_vendor.o((...args) => $options.onExerciseTypeChange && $options.onExerciseTypeChange(...args)),
    k: $data.exerciseTypeIndex,
    l: $data.exerciseTypes,
    m: $data.formData.duration,
    n: common_vendor.o(($event) => $data.formData.duration = $event.detail.value),
    o: $data.formData.calories,
    p: common_vendor.o(($event) => $data.formData.calories = $event.detail.value),
    q: common_vendor.t($data.formData.time),
    r: common_vendor.o((...args) => $options.onTimeChange && $options.onTimeChange(...args)),
    s: $data.formData.time,
    t: common_vendor.o((...args) => $options.closeModal && $options.closeModal(...args)),
    v: common_vendor.o((...args) => $options.saveExercise && $options.saveExercise(...args))
  } : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-97bb78b8"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/ExerciseManagement/ExerciseManagement.js.map
