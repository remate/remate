var photoGallery = angular.module('photoGallery',["ui.router"]);
photoGallery.config(function($stateProvider, $urlRouterProvider){
    $urlRouterProvider.otherwise('home');
    $stateProvider
        .state('content',{
            url: '/',
            views:{
                "":{templateUrl: 'content.html'},
                "header@content":{templateUrl: 'header.html'},
            }
        })
        .state('content.home',{
            url: 'home',
            views:{
                "body@content":{templateUrl: 'home.html'}
            }
        })
        .state('content.photos',{
            url: 'photos',
            views:{
                "body@content":{templateUrl: 'photos.html'}
            }
        })
        .state('content.about',{
            url:'about',
            views:{
                "body@content":{templateUrl: 'about.html'}
            }
        })
})