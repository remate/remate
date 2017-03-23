var testApp=angular.module('testapp',['ngRoute','testapp2']);
testApp.controller('testController',function($scope,$http){

    console.log(angular.lowercase('aasS'));
<<<<<<< HEAD
    $scope.switcher='home';
    $scope.title='home';
    $scope.myFunc=function(){
        console.log(2)
    };
=======
    $scope.switcher=function(){
        alert(1)
    }
>>>>>>> 20801a42403bedb95ce155671de07d3cfa579aa4

    $http({
        url:'index.json',
        method:'GET'
    }).then(function(data){
        //alert(data)
    },function(data){alert(data)})
    $scope.yourName='yourname';
    $scope.color='a';
    $scope.colorc=true;
    $scope.colord=false;
    $scope.ngShow=true;
    $scope.changeColor=function(){
        $scope.color='b';
        $scope.colorc=!$scope.colorc;
        $scope.colord=!$scope.colord;
        $scope.ngShow=!$scope.ngShow;
    };
    $scope.howtoloader=function(){
        console.log(1)
    };
    $scope.howtoloader2=function(){
        console.log(2)
    };
}).config(function($routeProvider){
    $routeProvider.when('/hello',{
        templateUrl:'index2.html',
        controller:'helloctrl'
    }).when('/haha',{
        templateUrl:'index3.html',
        controller:'helloctrls'
    }).otherwise({
        templateUrl:'index2.html',
        controller:'helloctrl'
    })
}).directive('loader',function(){
    return{
        restrict:'AE',
        link:function(scope,element,attrs){
            element.bind('mouseenter',function(){
                //attr.howtoloader();
                scope.$apply(attrs.howtoload);
            })
        }
    }
}).directive('myDirective',function(){
   return {
       scope:{title:'=',newFunc:'&myFunc',info:'@'},
       template:'<div ng-click="newFunc()">{{title}} : {{info}}</div>',
       link:function(scope,elem,attr){
           //scope.title='dada'
           //console.log(attr.inf)
       }
   }
}).directive('add',function(){
    return {
        template:'<div ng-click="addnum()">{{numnow}}</div>',
        link:function(scope,elem,attr){
            scope.addnum=function(){
                scope.numnow=scope.numnow+1;
                scope.$watch('numnow',function(newValue,oldValue){
                    if(newValue>15){
                        scope.numnow=11;
                    }
                })
            }
        }
    }
});