"use strict";
const common_vendor = require("../../common/vendor.js");
const _sfc_main = {
  data() {
    return {
      showAddModal: false,
      userInfo: {
        height: 175,
        weight: 70
      },
      newWeight: {
        weight: "",
        date: "",
        note: ""
      },
      weightRecords: []
      // Initializing with an empty array to load from API
    };
  },
  computed: {
    currentBMI() {
      return this.calculateBMI(this.userInfo.weight);
    },
    bmiStatus() {
      const bmi = parseFloat(this.currentBMI);
      if (bmi < 18.5)
        return "偏瘦";
      if (bmi < 24)
        return "正常";
      if (bmi < 28)
        return "偏胖";
      return "肥胖";
    },
    bmiStatusClass() {
      const bmi = parseFloat(this.currentBMI);
      if (bmi < 18.5)
        return "underweight";
      if (bmi < 24)
        return "normal";
      if (bmi < 28)
        return "overweight";
      return "obese";
    }
  },
  onLoad() {
    this.newWeight.date = this.getCurrentDate();
    this.loadUserInfo();
    this.loadWeightRecords();
  },
  onShow() {
    this.loadUserInfo();
  },
  methods: {
    onDateChange(e) {
      this.newWeight.date = e.detail.value;
    },
    addWeightRecord() {
      if (!this.newWeight.weight || !this.newWeight.date) {
        common_vendor.index.showToast({
          title: "请填写完整信息",
          icon: "none"
        });
        return;
      }
      const weight = parseFloat(this.newWeight.weight);
      this.calculateBMI(weight);
      const weightData = {
        weight,
        recordedAt: this.newWeight.date + "T12:00:00",
        note: this.newWeight.note
      };
      const token = common_vendor.index.getStorageSync("token");
      common_vendor.index.request({
        url: "http://localhost:8088/api/weight-records",
        method: "POST",
        data: weightData,
        header: {
          "Authorization": token ? "Bearer " + token : ""
        },
        success: (res) => {
          if (res.data.code === 0) {
            common_vendor.index.showToast({
              title: "记录成功",
              icon: "success"
            });
            this.loadWeightRecords();
            this.showAddModal = false;
            this.newWeight = { weight: "", date: this.getCurrentDate(), note: "" };
          } else {
            common_vendor.index.showToast({ title: "保存失败", icon: "none" });
          }
        },
        fail: () => {
          common_vendor.index.showToast({
            title: "保存失败，请检查网络",
            icon: "none"
          });
          this.showAddModal = false;
          this.newWeight = { weight: "", date: this.getCurrentDate(), note: "" };
        }
      });
    },
    loadUserInfo() {
      const token = common_vendor.index.getStorageSync("token");
      const storedUserInfo = common_vendor.index.getStorageSync("userInfo");
      if (storedUserInfo) {
        this.userInfo = { ...this.userInfo, ...storedUserInfo };
      }
      if (!token) {
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
            this.updateBMIForAllRecords();
          }
        }
      });
    },
    updateBMIForAllRecords() {
      if (!this.userInfo.height || this.userInfo.height <= 0) {
        return;
      }
      this.weightRecords.forEach((record) => {
        record.bmi = this.calculateBMI(record.weight);
      });
    },
    calculateBMI(weight) {
      if (!this.userInfo.height || this.userInfo.height <= 0 || !weight) {
        return 0;
      }
      const height = this.userInfo.height / 100;
      return parseFloat((weight / (height * height)).toFixed(1));
    },
    loadWeightRecords() {
      const token = common_vendor.index.getStorageSync("token");
      if (!token) {
        return;
      }
      common_vendor.index.request({
        url: "http://localhost:8088/api/weight-records",
        method: "GET",
        header: {
          "Authorization": "Bearer " + token
        },
        success: (res) => {
          if (res.data.code === 0) {
            const records = res.data.data.records || [];
            records.sort((a, b) => new Date(b.recordedAt) - new Date(a.recordedAt));
            this.weightRecords = records.map((record, index) => {
              const prevRecord = records[index + 1];
              const change = prevRecord ? (record.weight - prevRecord.weight).toFixed(1) : 0;
              const changeText = change > 0 ? `+${change}kg` : `${change}kg`;
              const changeClass = change > 0 ? "increase" : change < 0 ? "decrease" : "stable";
              const bmi = this.calculateBMI(record.weight);
              return {
                id: record.id,
                date: record.recordedAt.split("T")[0],
                weight: record.weight,
                bmi,
                change: changeText,
                changeClass
              };
            });
            if (this.weightRecords.length > 0) {
              this.userInfo.weight = this.weightRecords[0].weight;
            }
          }
        }
      });
    },
    getCurrentDate() {
      const now = /* @__PURE__ */ new Date();
      return now.getFullYear() + "-" + (now.getMonth() + 1).toString().padStart(2, "0") + "-" + now.getDate().toString().padStart(2, "0");
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.o(($event) => $data.showAddModal = true),
    b: common_vendor.t($options.currentBMI),
    c: common_vendor.t($options.bmiStatus),
    d: common_vendor.n($options.bmiStatusClass),
    e: common_vendor.t($data.userInfo.height),
    f: common_vendor.t($data.userInfo.weight),
    g: common_vendor.f($data.weightRecords, (record, k0, i0) => {
      return {
        a: common_vendor.t(record.date),
        b: common_vendor.t(record.weight),
        c: common_vendor.t(record.bmi),
        d: common_vendor.t(record.change),
        e: common_vendor.n(record.changeClass),
        f: record.id
      };
    }),
    h: $data.showAddModal
  }, $data.showAddModal ? {
    i: $data.newWeight.weight,
    j: common_vendor.o(($event) => $data.newWeight.weight = $event.detail.value),
    k: common_vendor.t($data.newWeight.date),
    l: $data.newWeight.date,
    m: common_vendor.o((...args) => $options.onDateChange && $options.onDateChange(...args)),
    n: $data.newWeight.note,
    o: common_vendor.o(($event) => $data.newWeight.note = $event.detail.value),
    p: common_vendor.o(($event) => $data.showAddModal = false),
    q: common_vendor.o((...args) => $options.addWeightRecord && $options.addWeightRecord(...args)),
    r: common_vendor.o(() => {
    }),
    s: common_vendor.o(($event) => $data.showAddModal = false)
  } : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-d2b1508b"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/WeightManagement/WeightManagement.js.map
