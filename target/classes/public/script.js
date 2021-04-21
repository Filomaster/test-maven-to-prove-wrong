console.log("It works!");

let update = () => {
  fetch("/all")
    .then((response) => response.json())
    .then((data) => (app.users = data));
};

const app = new Vue({
  el: "#app",
  data: {
    users: [],
    sortSelection: "num",
    selectedRecord: null,
    isWindowShowed: false, //!Change to false
    modifyUser: null,
    type: null,
    title: "",
    canClick: false,
  },
  methods: {
    deleteUser() {
      fetch("/delete", {
        method: "DELETE",
        body: JSON.stringify({ id: app.selectedRecord.id }),
      }).then(() => {
        update();
        app.selectedRecord = null;
      });
    },
    purgeUsers() {
      fetch("/purge", { method: "DELETE" }).then(() => update());
    },
    showWindow(type) {
      app.type = type;
      app.isWindowShowed = true;
      app.title =
        app.type == "add"
          ? "ADD NEW RECORD"
          : app.type == "populate"
          ? "ADD MULTIPLE RECORDS"
          : "EDIT RECORD";
    },

    action() {
      if (!app.canClick) return;
      console.log(app.modifyUser);
      switch (app.type) {
        case "add":
          fetch("/add", {
            method: "POST",
            body: JSON.stringify(app.modifyUser),
          }).then(() => {
            update();
            app.modifyUser = null;
            app.isWindowShowed = false;
            app.canClick = false;
          });
          break;
        case "edit":
          fetch("/update", {
            method: "PUT",
            body: JSON.stringify(app.modifyUser),
          }).then(() => {
            update();
            app.modifyUser = null;
            app.selectedRecord = null;
            app.isWindowShowed = false;
            app.canClick = false;
          });
          break;
      }
    },
    validate() {
      let name = document.getElementById("name").value;
      let surname = document.getElementById("surname").value;
      let age = document.getElementById("age").value;
      let pesel = document.getElementById("pesel").value;

      if (name && surname && age && pesel) {
        console.log("ok");
        if (!app.modifyUser) {
          app.modifyUser = {
            name: name,
            surname: surname,
            age: parseInt(age),
            pesel: pesel,
          };
          app.canClick = true;
        } else {
          app.modifyUser.name = name;
          app.modifyUser.surname = surname;
          app.modifyUser.age = parseInt(age);
          app.modifyUser.pesel = pesel;
          app.canClick = true;
        }
      } else {
        console.log("missing");
        app.canClick = false;
      }
    },
    validateAge(e) {
      console.log(e.key);
      if (!/[0-9]/.test(e.key)) return;
    },

    // setName(e) {
    //   app.usr =
    // },
  },
});

update();

let action = (reqType) => {
  switch (reqType) {
    case "edit":
      break;
    case "delete":
      fetch("/delete", {
        method: "DELETE",
        body: { id: app.selectedRecord.id },
      });
      break;
    case "purge":
      fetch("/purge", { method: "DELETE" });
      break;
    case "populate":
      break;
  }
  update();
};
