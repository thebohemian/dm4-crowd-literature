angular.module("crowdedit")
.controller("EditPersonController", function($scope, $routeParams, $location, crowdService) {

    var loadPerson = function(personId) {
      crowdService.getEditablePerson(personId, function(response) {
        $scope.person = response.data;
        $scope.isUpdateBlocked = false;

        // Resets new data
        $scope.newData = {
          email: "",
          address: {
            street: "",
            postalCode: "",
            city: "",
            country: ""
          },
          url: "",
          nationality: "",
          language: ""
        };

      });
    };

    var updatePerson = function() {
      var person = $scope.person;
      // Prevent further updates until the last write hasn't been followed by
      // a reload.
      if (!$scope.isUpdateBlocked) {
        $scope.isUpdateBlocked = true;

        // Reloads the person completely automatically
        crowdService.updatePerson(person, function(response) {
          loadPerson(person.id);
        });

      }
    };

    var validateAndLoadPerson = function() {
      var errorCallback = function(errorResponse) {
        $location.path("/error-noperson");
      };

      crowdService.validateSetup(function(validateResponse) {
        crowdService.getPersonIdByLoggedInUser(function(response) {
            var personId = response.data;
            loadPerson(personId);
        }, errorCallback);
      }, errorCallback);
    };

    $scope.newData = {};

    $scope.isUpdateBlocked = false;

    // Autoload
    validateAndLoadPerson();

    var addNewMultivalueElement = function(dmTopicType, newDataField, skipUpdatePerson) {
      var person = $scope.person;
      alert("adding to " + dmTopicType + "- value: " + $scope.newData[newDataField]);

      if (!person['childs'][dmTopicType]) {
        person['childs'][dmTopicType] = [];
      }

      person['childs'][dmTopicType].push({
        uri: "",
        type_uri: dmTopicType,
        value: $scope.newData[newDataField]
      });

      $scope.newData[newDataField] = "";

      if (!skipUpdatePerson) {
        updatePerson();
      }
    };

    $scope.addNewEmail = function() {
      addNewMultivalueElement('dm4.contacts.email_address', 'email');
    };

    $scope.addNewURL = function() {
      addNewMultivalueElement('dm4.webbrowser.url', 'url');
    };

    $scope.addNewLanguage = function() {
      addNewMultivalueElement('crowd.language', 'language');
    };

    $scope.addNewNationality = function() {
      addNewMultivalueElement('crowd.person.nationality', 'nationality');
    };

    $scope.handleSubmit = function() {
      $scope.newData.email && addNewMultivalueElement('dm4.contacts.email_address', 'email', true);
      $scope.newData.url && addNewMultivalueElement('dm4.webbrowser.url', 'url');
      $scope.newData.language && addNewMultivalueElement('crowd.language', 'language');
      $scope.newData.nationality && addNewMultivalueElement('crowd.person.nationality', 'nationality');

      updatePerson();
    };

    var moveToTrash = function(array, index) {
      var old = array[index];

      if (old.id) {
        // Replace with a trash object
        array[index] = "del_id:" + old.id;
      } else {
        // Has not been saved yet. Just throw away the array entry
        array.splice(index, 1);
      }
    };

    var removeMultivalueElement = function(dmTopicType, index) {
      moveToTrash($scope.person['childs'][dmTopicType], index);

      updatePerson();
    };

    $scope.removeURL = function(index) {
      removeMultivalueElement('dm4.webbrowser.url', index);
    };

    $scope.removeLanguage = function(index) {
      removeMultivalueElement('crowd.language', index);
    };

    $scope.removeNationality = function(index) {
      removeMultivalueElement('crowd.person.nationality', index);
    };

    $scope.removeEmail = function(index) {
      removeMultivalueElement('dm4.contacts.email_address', index);
    };

    $scope.addNewAddress = function() {
      var person = $scope.person;

      if (!person['childs']['dm4.contacts.address#dm4.contacts.address_entry']) {
        person['childs']['dm4.contacts.address#dm4.contacts.address_entry'] = [];
      }

      person['childs']['dm4.contacts.address#dm4.contacts.address_entry'].push({
        uri: "",
        type_uri: "dm4.contacts.address",
        childs: {
          "dm4.contacts.street": {
            type_uri: "dm4.contacts.street",
            value: $scope.newData.address.street
          },
          "dm4.contacts.postal_code": {
            type_uri: "dm4.contacts.postal_code",
            value: $scope.newData.address.postalCode
          },
          "dm4.contacts.city": {
            type_uri: "dm4.contacts.city",
            value: $scope.newData.address.city
          },
          "dm4.contacts.country": {
            type_uri: "dm4.contacts.country",
            value: $scope.newData.address.country
          }
        }
      });

      $scope.newData.address = {
        street: "",
        postalCode: "",
        city: "",
        country: ""
      };

      updatePerson();
    };

    $scope.removeAddress = function(index) {
      moveToTrash($scope.person['childs']['dm4.contacts.address#dm4.contacts.address_entry'], index);

      updatePerson();
    };

    $scope.updatePerson = updatePerson;

    $scope.logout = function() {
      crowdService.logout(function() {
        $location.path("/start");
      });
    };

    $scope.killId = function(topic) {
      delete topic.id;
    };

    $scope.modify = function(dmTopicType, topic){
      if (topic.id) {
        var delObj = {
          del_id: topid.id
        };

        $scope.person['childs'][dmTopicType].push(delObj);

        delete topic.id;
      }
    };

    $scope.ckEditorOptions = {
         // Avoid loading a config file
         customConfig: "",

         // The toolbar groups arrangement, optimized for a single toolbar row.
         toolbarGroups: [
             {name: "document",    groups: ["mode", "document", "doctools"]},
             {name: "clipboard",   groups: ["clipboard", "undo"]},
             {name: "editing",     groups: ["find", "selection", "spellchecker"]},
             {name: "forms"},
             {name: "basicstyles", groups: ["basicstyles", "cleanup"]},
             {name: "paragraph",   groups: ["list", "blocks", "align", "bidi"]},
             {name: "links"},
             {name: "insert"},
             {name: "styles"},
             {name: "colors"},
             {name: "tools"},
             {name: "others"},
             {name: "about"}
         ],

         // The default plugins included in the basic setup define some buttons that
         // are not needed in a basic editor. They are removed here.
         removeButtons: "Cut,Copy,Paste,Undo,Redo,Anchor,Underline,Strike,Subscript,Superscript",

         // Remove the "advanced" tab from the "link" dialog
         removeDialogTabs: "link:advanced",

         contentsCss: "/de.deepamehta.webclient/css/ckeditor-contents.css",

         stylesSet: [
             // Block styles
             {name: "Paragraph",   element: "p"},
             {name: "Heading 1",   element: "h1"},
             {name: "Heading 2",   element: "h2"},
             {name: "Heading 3",   element: "h3"},
             {name: "Heading 4",   element: "h4"},
             {name: "Code Block",  element: "pre",  attributes: {class: "code-block"}},
             {name: "Block Quote", element: "div",  attributes: {class: "blockquote"}},
             // Inline Styles
             {name: "Code",        element: "code"},
             {name: "Marker",      element: "span", attributes: {class: "marker"}}
         ],

         autoGrow_onStartup: true
     };
})
