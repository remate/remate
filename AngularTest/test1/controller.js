var testApp2=angular.module('testapp2',[]);
testApp2.controller('helloctrl',function($scope){
    $scope.himName='himname';
}).controller('helloctrls',function($scope){
    $scope.herName='hername';
    $scope.b=[{c:12},{c:13}];
}).directive('hello',function(){
    return{
        restrict:'ACME',
        transclude:true,
        template:'<div class="color_a">hi<div ng-transclude></div></div>',
        //使hello里的div不被末班内容覆盖使用transclude
        replace:true
    }
})
