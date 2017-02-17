photoGallery.controller('HomeController',['$scope','$http',function($scope,$http){
    var that = this;
    this.backhome=function(){
            $http({
                url:'index.json',
                method:'GET',
            }).then(function(data){
                that.who=data.data[0].name;
                //这里不能用this是做用户问题  但可以先赋值
            })
    }
}])