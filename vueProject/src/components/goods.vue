<template>
   <div class="goods">

       <div class="menu-wrapper">

       </div>
       <div class="foods-wrapper">

       </div>
   </div>
</template>
<script type="text/ecmascript-6">
    /* eslint-disable */
    export default {
        props:['seller','goodthings'],
        created() {
            console.log(this.goodthings[0])
        }
    }
</script>
<style lang="stylus" rel="stylesheet/stylus">
    .goods
        position: absolute
        display flex
        top: 174px
        bottom: 46px
        width:100%
        overflow hidden
        .menu-wrapper
            flex 0 0 80px
            width:80px
            background #f3f5f7
        .foods-wrapper
            flex:1
</style>
