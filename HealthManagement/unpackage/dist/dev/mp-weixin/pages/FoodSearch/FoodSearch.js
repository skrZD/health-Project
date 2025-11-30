"use strict";
const common_vendor = require("../../common/vendor.js");
const _sfc_main = {
  data() {
    return {
      keyword: "",
      activeCat: null,
      categories: [
        { id: 0, name: "全部" },
        { id: 1, name: "主食" },
        { id: 2, name: "肉蛋奶" },
        { id: 3, name: "蔬果" }
      ],
      foods: []
    };
  },
  onLoad() {
    this.search();
  },
  methods: {
    selectCat(id) {
      this.activeCat = id;
      this.search();
    },
    search() {
      const params = {
        q: this.keyword,
        page: 1,
        size: 50
      };
      if (this.activeCat !== 0) {
        params.categoryId = this.activeCat;
      }
      common_vendor.index.request({
        url: "http://localhost:8088/api/foods/search",
        method: "GET",
        data: params,
        success: (res) => {
          if (res.data.code === 0) {
            this.foods = res.data.data.records || [];
          } else {
            common_vendor.index.showToast({ title: "搜索失败", icon: "none" });
          }
        },
        fail: () => {
          const all = [
            { id: 101, name: "鸡胸肉", caloriesPer100: 165 },
            { id: 102, name: "米饭", caloriesPer100: 116 },
            { id: 103, name: "西兰花", caloriesPer100: 33 }
          ];
          this.foods = all.filter((x) => !this.keyword || x.name.includes(this.keyword));
        }
      });
    },
    chooseFood(food) {
      const eventChannel = this.getOpenerEventChannel();
      const selectedFood = {
        ...food,
        calories_per_100: food.caloriesPer100
      };
      eventChannel.emit("foodSelected", selectedFood);
      common_vendor.index.navigateBack();
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.o((...args) => $options.search && $options.search(...args)),
    b: $data.keyword,
    c: common_vendor.o(($event) => $data.keyword = $event.detail.value),
    d: common_vendor.o((...args) => $options.search && $options.search(...args)),
    e: common_vendor.f($data.categories, (c, k0, i0) => {
      return {
        a: common_vendor.t(c.name),
        b: $data.activeCat === c.id ? 1 : "",
        c: c.id,
        d: common_vendor.o(($event) => $options.selectCat(c.id), c.id)
      };
    }),
    f: common_vendor.f($data.foods, (f, k0, i0) => {
      return {
        a: common_vendor.t(f.name),
        b: common_vendor.t(f.caloriesPer100),
        c: f.id,
        d: common_vendor.o(($event) => $options.chooseFood(f), f.id)
      };
    }),
    g: $data.foods.length === 0
  }, $data.foods.length === 0 ? {} : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-2b434971"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/FoodSearch/FoodSearch.js.map
