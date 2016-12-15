//index.js
//获取应用实例
var app = getApp()
console.log(app.globalData.userName)
Page({
  data: {
    userInfo: {
      avatarUrl:"http://xquser.xiangqu.com/FsOANBP51aSamPo4GZ1sstQFgWrj?imageView2/2/w/140/q/90/format/jpg/180x180/"
    },
    count:1,
    isManager:"false",
    loop:[1, 2, 3],
    staffA: {firstName: 'Hulk', lastName: 'Hu'},
    staffB: {firstName: 'Shang', lastName: 'You'}
  },
  //事件处理函数
  bindViewTap: function() {
    wx.navigateTo({
      url: '../logs/logs'
    })
  },
  like:function(e){
    this.setData({
      count: this.data.count + 1
    });
    console.log(e)
  },
  requestManager:function(){
    console.log("提交申请")
    this.setData({
      isManager:"true"
      })
  },
  onLoad: function () {
    console.log('onLoad');
    var that = this
  	//调用应用实例的方法获取全局数据
    app.getUserInfo(function(userInfo){
      //更新数据
      that.setData({
        userInfo:userInfo
      })
      that.update()
    })
  }
})
