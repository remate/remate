var testApp2=angular.module('testapp2',[]);
testApp2.controller('helloctrl',function($scope){
    $scope.himName='himname';
}).controller('helloctrls',function($scope){
    $scope.herName='hername';
    $scope.b=[{c:12},{c:13}]
});
