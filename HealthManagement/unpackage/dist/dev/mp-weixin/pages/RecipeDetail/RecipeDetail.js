"use strict";
const common_vendor = require("../../common/vendor.js");
const api_config = require("../../api/config.js");
const _sfc_main = {
  data() {
    return {
      recipe: {
        name: "",
        description: "",
        category: null
      },
      recipeItems: []
    };
  },
  onLoad(options) {
    if (options.recipeId) {
      this.loadRecipeDetail(options.recipeId);
    }
  },
  methods: {
    // 加载食谱详情
    loadRecipeDetail(recipeId) {
      common_vendor.index.request({
        url: `${api_config.apiConfig.API_CONFIG.BASE_URL}${api_config.apiConfig.API_CONFIG.RECIPES.DETAIL_WITH_ITEMS}/${recipeId}/detail`,
        method: "GET",
        success: (res) => {
          if (res.data.code === 0) {
            const data = res.data.data;
            this.recipe = data.recipe;
            this.recipeItems = data.items || [];
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
    // 加载食谱明细（备用方法）
    loadRecipeItems(recipeId) {
      common_vendor.index.request({
        url: `${api_config.apiConfig.API_CONFIG.BASE_URL}${api_config.apiConfig.API_CONFIG.RECIPES.ITEMS}/${recipeId}`,
        method: "GET",
        success: (res) => {
          if (res.data.code === 0) {
            this.recipeItems = res.data.data || [];
          }
        },
        fail: () => {
          common_vendor.index.__f__("log", "at pages/RecipeDetail/RecipeDetail.vue:97", "加载食谱明细失败");
        }
      });
    },
    // 使用食谱
    useRecipe() {
      common_vendor.index.navigateTo({
        url: `/pages/DietRecordDetail/DietRecordDetail?recipeId=${this.recipe.id}&recipeName=${encodeURIComponent(this.recipe.name)}`
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  var _a;
  return common_vendor.e({
    a: common_vendor.t($data.recipe.name),
    b: common_vendor.t(((_a = $data.recipe.category) == null ? void 0 : _a.name) || "未分类"),
    c: $data.recipe.description
  }, $data.recipe.description ? {
    d: common_vendor.t($data.recipe.description)
  } : {}, {
    e: common_vendor.f($data.recipeItems, (item, k0, i0) => {
      return {
        a: common_vendor.t(item.foodName || "未知食材"),
        b: common_vendor.t(item.quantity),
        c: common_vendor.t(item.quantityUnit),
        d: item.id
      };
    }),
    f: common_vendor.o((...args) => $options.useRecipe && $options.useRecipe(...args))
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-5739840f"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/RecipeDetail/RecipeDetail.js.map
