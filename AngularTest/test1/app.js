var testApp=angular.module('testapp',['ngRoute','testapp2']);
testApp.controller('testController',function($scope){
    $scope.yourName='yourname';
}).config(function($routeProvider){
    $routeProvider.when('/hello',{
        templateUrl:'index2.html',
        controller:'helloctrl'
    }).when('/haha',{
        templateUrl:'index3.html',
        controller:'helloctrls'
    })
})