CKEDITOR.editorConfig = function(config) {
  config.removePlugins += 'about,allyhelp,button,print,maximize,newpage,pagebreak,resize,scayt,smiley,wsc,' + config.removePlugins;
  config.toolbar_Full =
      [
        ['Preview','-','ShowBlocks'],
        ['Link','Unlink','Anchor'],
        ['SpecialTag','Image','Table','SpecialChar'],
        ['Form', 'Checkbox', 'Radio', 'TextField', 'Textarea', 'Select', 'Button', 'ImageButton', 'HiddenField'],
        '/',
        ['FontSize','TextColor','BGColor'],
        ['Bold','Italic','Underline','Strike'],
        ['NumberedList','BulletedList','-','Outdent','Indent','Blockquote'],
        ['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],
        ['RemoveFormat']
      ];

  config.fillEmptyBlocks = false;
  config.ignoreEmptyParagraph = true;
  config.language = 'ru';
};
