<template>
    <div class="header">
        <div class="content-wrapper">
            <div class="avatar">
                <img width="64" height="64" :src="seller.avatarPath"/>
            </div>
        </div>
        <div class="bulletion-wrapper"></div>
    </div>
</template>

<script type="text/ecmascript-6">
    export default {
        props: {
            seller: {
                type: Object
            }
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus">
    .header
        text-align center
        border-bottom 1px solid black
</style>
