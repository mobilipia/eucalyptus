define([
  'text!./template.html',
  'rivets',
  ], function( template, rivets ) {
    return Backbone.View.extend({
      
      initialize: function(args) {
        this.$el.html(template);
        this.rView = rivets.bind(this.$el, args.model);    
      },

      add: function() {

      },

      remove: function() {

      },
  
      render: function() {
        this.rView.sync();
        return this;
      }

    });
 });
