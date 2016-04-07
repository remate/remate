define(['plugins/sortable/jquery.sortable','css!plugins/sortable/sort'], function(sort,css) {
    return function($el) {
        $el.sortable({
            axis: 'y',
            containment: 'parent',
            containerSelector: 'table',
            itemPath: '> tbody',
            handle: '.desc-sort',
            cursor: 'move',
            itemSelector: 'tr',
            placeholder: '<tr />'
        });
    }
});