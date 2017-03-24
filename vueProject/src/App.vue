<template>
  <div id="app">
      <v-header :seller="seller"></v-header>
  <div class="tab border-1px">
    <router-link to="/goods" class="tab-item" >商品</router-link>
      <router-link to="/comments" class="tab-item">评论</router-link>
      <router-link to="/sellers" class="tab-item">商家</router-link>
  </div>
    <router-view></router-view>
  </div>
</template>

<script type="text/ecmascript-6">
//    注册header组件
// eslint-disable-next-line
/* eslint-disable */
    import header from './components/header.vue';
    export default {
        data() {
            return {
                seller:{}
            }
        },
        created() {
            this.$http.get('../static/api/seller.json').then((res) => {
//                console.log(res);
                var response = res.body;
                this.seller = response.seller;
            });
        },
        components:{
          'v-header':header
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus">
    @import "../static/border_1px.styl"
bgcolor(el)
    background el
.tab
  border-1px(black)
  display flex
  /*justify-content space-around*/
  font-size 15px
  .tab-item
    color: #000
    flex 1
    text-align center
    height 30px
    line-height 30px
    bgcolor(#eee)
    &:hover
        bgcolor(#ddd)
    &.active
        bgcolor(#999)
</style>
