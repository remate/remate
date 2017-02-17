var photoGallery = angular.module('photoGallery',["ui.router"]);
photoGallery.config(function($stateProvider, $urlRouterProvider){
    $urlRouterProvider.otherwise('home');
    $stateProvider
        .state('content',{
            url: '/',
            abstract: true,
            views:{
                "":{templateUrl: 'content.html'},
                "header@content":{templateUrl: 'header.html'},
            }
        })
        .state('content.home',{
            url: 'home',
            views:{
                "body@content":{
                    templateUrl: 'home.html',
                    controller: 'HomeController',
                    controllerAs: 'ctrHome'
                }
            }
        })
        .state('content.photos',{
            url: 'photos',
            abstract: true,
            views:{
                "body@content":{
                    templateUrl: 'photos.html',
                    controller: 'PhotoController',
                    controllerAs: 'ctrPhoto'
                }
            }
        })
        .state('content.photos.list',{
            url: '/list',
            templateUrl: 'photos-list.html',
            controller: "PhotoListController",
            controllerAs: 'ctrPhotoList'
        })
        .state('content.photos.detail',{
            url: '/detail',
            templateUrl: 'photos-detail.html',
            controller: 'PhotoDetailController',
            controllerAs: 'ctrPhotoDetail'
        })
        .state('content.photos.detail.comment',{
            url: '/comment',
            templateUrl: 'photos-detail-comment.html'
        })
        .state('content.about',{
            url:'about',
            views:{
                "body@content":{templateUrl: 'about.html'}
            }
        })
})