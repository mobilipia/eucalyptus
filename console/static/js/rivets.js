rivets.configure({
	adapter: {
        iterate: function(obj, callback) {
            //console.log('iterate:', obj, callback);
            if (obj instanceof Backbone.Collection) {
                var l = obj.length;
                for (var i = 0; i < l; i++) {
                    callback(obj.at(i), i);
                }
            } else if (obj instanceof Backbone.Model) {
                var keys = obj.keys();
                for (var i = 0; i < keys.length; i++) {
                    callback(obj.get(keys[i]), keys[i]);
                }
            } else if (obj instanceof Array) {
                for (var i = 0; i < obj.length; i++) {
                    callback(obj[i], i);
                }
            } else {
                for (var i in obj) {
                    callback(obj[i], i);
                }
            }
        },
	    subscribe: function(obj, keypath, callback) {
            if (obj instanceof Backbone.Model) {
                obj.on('change:' + keypath, function (m, v) { callback(v) });
            } else if (obj instanceof Backbone.Collection) {
                obj.on('add remove reset', function () { 
                    callback(obj.at(keypath));
                });
            } else {
                console.log('plain object');
            }
	    },
	    unsubscribe: function(obj, keypath, callback) {
            if (obj instanceof Backbone.Model)  {
                obj.off('change:' + keypath, function (m, v) { callback(v) });
            } else if (obj instanceof Backbone.Collection) {
                obj.off('add remove reset', function () { 
                    callback(obj.at(keypath)) 
                });
            } else {
                console.log('plain object');
            }
	    },
	    read: function(obj, keypath) {
            if (obj == null) return null;
            if (typeof keypath === 'undefined' || keypath === '') return obj;

            if (obj instanceof Backbone.Model)  {
                return obj.get(keypath);
            } else if (obj instanceof Backbone.Collection)  {
                return obj.at(keypath);
            } else {
                return obj[keypath];
            }
	    },
	    publish: function(obj, keypath, value) {
            if (obj instanceof Backbone.Model)  {
                obj.set(keypath, value);
            } else if (obj instanceof Backbone.Collection) {
                obj.at(keypath).set(value);
            } else {
                obj[keypath] = value;
            }
	    }
	}
});

rivets.binders["ui-*"] = {
    bind: function(el) {
        var self = this;
        require(['views/ui/' + this.args[0] + '/index'], function(view) {
            self.bbView = new view({
                model: self.bbLastValue ? self.bbLastValue : {},
                innerHtml: $(el).html()
            });
            $(el).replaceWith($(self.bbView.el).children());
            return self.bbView.el;
        });
    },
    routine: function(el, value) {
        this.bbLastValue = value;
        if (this.bbView) {
           this.bbView.model = value;
           this.bbView.render();
        }
    }
}

rivets.binders["addclass"] = {
    routine: function(el, value) {
        $(el).addClass(value);
    }
}

rivets.binders["msg"] = {
    tokenizes: true,
    routine: function(el, keyname) {
      var value = window[this.keypath];

      if (value == null) return;

      if (el.innerText != null) {
        return el.innerText = value != null ? value : '';
      } else {
        return el.textContent = value != null ? value : '';
      }
    }
}
