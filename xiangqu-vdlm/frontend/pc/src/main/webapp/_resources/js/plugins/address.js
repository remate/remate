

/**
 * 省市区联动组件
 * @return {[Object]}        [Address]
 * @example
 *  addr.init(3,$('.shop-province'),$('.shop-city'),$('.shop-area'),{
        province : 234,
        city:235,
        district : 300
    });
 */
;define(['jquery', 'base/utils', 'amaze'], function(jquery, utils, amaze) {
    function Address() {}
    Address.prototype.init = function(leave, $pro, $city, $district, initData) {
        var self = this;
        this.pro = $pro,
            this.city = $city,
            this.district = $district,
            this.leave = leave;
        var initData = initData.province ? initData : {
            province: 2,
            city: 4
        };


        this.renderProvince(function() {
            if (initData && initData.province) {
                self.pro.find('option[value="' + initData.province + '"]').attr('selected', 'selected');
                self.pro.trigger("chosen:updated.chosen"); 
                self.changePro();
            }
        });
        if (initData && initData.province) {
            this.renderCity(initData.province, function() {
                //先判断是否有默认的市,如果有，先渲染 data.city为市的id
                if (initData && initData.city) {
                    self.city.find('option[value="' + initData.city + '"]').attr('selected', 'selected');
                    self.city.trigger("chosen:updated.chosen"); 
                }
            });
        }
        if (self.leave == 3 && initData && initData.city) {
            this.renderDistrict(initData.city, function() {
                //先判断是否有默认的市,如果有，先渲染 data.city为市的id
                if (initData && initData.district) {
                    self.district.find('option[value="' + initData.district + '"]').attr('selected', 'selected');
                    self.district.trigger("chosen:updated.chosen"); 
                }
            });
        }
        this.changePro();
        if (self.leave == 3) {
            this.changecity();
        }
    }
    Address.prototype.renderProvince = function(cb) {
        var self = this;
        utils.api.address(1, function(data) {
            self.pro.empty();
            $.each(data.data, function(i, prov) {
                self.pro.append('<option value="' + prov.id + '">' + prov.name + '</option>')
            });
            self.pro.find('option:eq(0)').attr('selected',true);
            self.pro.trigger("chosen:updated.chosen");
            cb && cb();
        });
    }
    Address.prototype.renderCity = function(id, cb) {
        var self = this;
        utils.api.address(id, function(data) {
            self.city.next('.chosen-container').show();
            self.city.empty();
            $.each(data.data, function(i, city) {
                self.city.append('<option value="' + city.id + '">' + city.name + '</option>')
            });
            self.city.find('option:eq(0)').attr('selected',true);
            if(self.leave == 3){
                self.renderDistrict( self.city.find('option:eq(0)').attr('value') );
            }
            self.city.trigger("chosen:updated.chosen");
            cb && cb();
        }, function() {
            if( self.district ){
                self.district.empty().next('.chosen-container').hide();
            }
            self.city.empty().next('.chosen-container').hide();
            self.city.trigger("chosen:updated.chosen");
            if(self.leave == 3){
                self.district.trigger("chosen:updated.chosen");
            }
        });
    }
    Address.prototype.renderDistrict = function(id, cb) {
        var self = this;
        utils.api.address(id, function(data) {
            self.district.next('.chosen-container').show();
            self.district.empty();
            $.each(data.data, function(i, district) {
                self.district.append('<option value="' + district.id + '">' + district.name + '</option>')
            });
            self.district.find('option:eq(0)').attr('selected',true);
            self.district.trigger("chosen:updated.chosen");
            cb && cb();
        }, function() {
            self.district.empty().next('.chosen-container').hide();
            self.district.trigger("chosen:updated.chosen");
        });
    }
    Address.prototype.changePro = function() {
        var self = this;
        self.pro.on('change', function() {
            var province = self.pro.val();
            self.renderCity(province);
        });
    }
    Address.prototype.changecity = function() {
        var self = this;
        self.city.on('change', function() {
            var city = self.city.val();
            if(self.leave == 3){
                self.renderDistrict(city);
            }
        });
    }
    return Address;
});

