"use strict";
const common_vendor = require("../../common/vendor.js");
const api_config = require("../../api/config.js");
const common_assets = require("../../common/assets.js");
const _sfc_main = {
  data() {
    return {
      recipes: [],
      categories: [],
      selectedCategoryId: null,
      loading: false
    };
  },
  onLoad() {
    this.loadCategories();
    this.loadRecipes();
  },
  methods: {
    // 加载分类
    loadCategories() {
      common_vendor.index.request({
        url: api_config.apiConfig.API_CONFIG.BASE_URL + "/recipe-categories",
        method: "GET",
        success: (res) => {
          if (res.data.code === 0) {
            this.categories = res.data.data || [];
          }
        },
        fail: () => {
          common_vendor.index.__f__("log", "at pages/Recipes/Recipes.vue:92", "加载分类失败");
        }
      });
    },
    // 加载食谱
    loadRecipes() {
      this.loading = true;
      const url = this.selectedCategoryId ? `${api_config.apiConfig.API_CONFIG.BASE_URL}${api_config.apiConfig.API_CONFIG.RECIPES.BY_CATEGORY}/${this.selectedCategoryId}` : `${api_config.apiConfig.API_CONFIG.BASE_URL}${api_config.apiConfig.API_CONFIG.RECIPES.ENABLED}`;
      common_vendor.index.request({
        url,
        method: "GET",
        success: (res) => {
          if (res.data.code === 0) {
            this.recipes = res.data.data || [];
          }
        },
        fail: () => {
          common_vendor.index.__f__("log", "at pages/Recipes/Recipes.vue:113", "加载食谱失败");
        },
        complete: () => {
          this.loading = false;
        }
      });
    },
    // 选择分类
    selectCategory(categoryId) {
      this.selectedCategoryId = categoryId;
      this.loadRecipes();
    },
    // 查看食谱详情
    viewRecipeDetail(recipe) {
      common_vendor.index.navigateTo({
        url: `/pages/RecipeDetail/RecipeDetail?recipeId=${recipe.id}`
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $data.selectedCategoryId === null ? 1 : "",
    b: common_vendor.o(($event) => $options.selectCategory(null)),
    c: common_vendor.f($data.categories, (category, k0, i0) => {
      return {
        a: common_vendor.t(category.name),
        b: $data.selectedCategoryId === category.id ? 1 : "",
        c: category.id,
        d: common_vendor.o(($event) => $options.selectCategory(category.id), category.id)
      };
    }),
    d: common_vendor.f($data.recipes, (recipe, k0, i0) => {
      var _a;
      return {
        a: common_vendor.t(recipe.name),
        b: common_vendor.t(recipe.description || "暂无描述"),
        c: common_vendor.t(((_a = recipe.category) == null ? void 0 : _a.name) || "未分类"),
        d: recipe.id,
        e: common_vendor.o(($event) => $options.viewRecipeDetail(recipe), recipe.id)
      };
    }),
    e: common_assets._imports_0$1,
    f: $data.recipes.length === 0 && !$data.loading
  }, $data.recipes.length === 0 && !$data.loading ? {} : {}, {
    g: $data.loading
  }, $data.loading ? {} : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-9c202826"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/Recipes/Recipes.js.map
