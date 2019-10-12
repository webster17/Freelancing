const electron = require('electron');
const url = require('url');
const path = require('path');

const {app, BrowserWindow, Menu} = electron;

//Set env
// process.env.NODE_ENV='production';

 let mainWindow;
 let addWindow;

  //Listen for app to be ready
  app.on('ready', function(){
    
    
   const {width, height} = electron.screen.getPrimaryDisplay().workAreaSize
    
    //create new window
    mainWindow = new BrowserWindow({width, height, minWidth:1024, minHeight:768});
    
    //load HTML into window
    mainWindow.loadURL(url.format({
        pathname: path.join(__dirname, 'src/index.html'),
        protocol: 'file:',
        slashes: true
    }));

    //quit app when closed
    mainWindow.on('closed', function(){
      app.quit();
    })

    
    mainWindow.maximize();

    // buld menu from template
    const mainMenu = Menu.buildFromTemplate(mainMenuTemplate);
    //Insert menu
    Menu.setApplicationMenu(mainMenu);
  });

  //Handle create Add Window
  function createAddWindow(){
    //create new window
    addWindow = new BrowserWindow({
      width : 200,
      height : 300,
      title : "Add new Profile"
    });

    //load HTML into window
    addWindow.loadURL(url.format({
        pathname: path.join(__dirname, 'login.html'),
        protocol: 'file:',
        slashes: true
    }));

    //Garbage collection handle
    addWindow.on('close', function(){
      addWindow = null;
    })
  }

  //create menu template
  const mainMenuTemplate = [
    {
      label: 'File',
      submenu: [
        {
          label: 'Add Item',
          click(){
            createAddWindow();
          }
        },
        {
          label: 'Clear Item'
        },
        {
          label: 'Quit',
          accelerator : process.platform == 'darwin' ? 'Command+Q' : 'Ctrl+Q',
          click(){
            app.quit();
          }
        }
      ]
    }
  ];

  //If Mac,  add an empty object to menu
  if(process.platform == 'darwin'){
    mainMenuTemplate.unshift({});
  }

  if(process.env.NODE_ENV !== 'production'){
    mainMenuTemplate.push({
      label: 'Developer Tools',
      submenu:[{
        label: 'Toggle DevTools',
        accelerator : process.platform == 'darwin' ? 'Command+I' : 'Ctrl+I',
        click(item, focusedWindow){
          focusedWindow.toggleDevTools();
        } 
      },{
        role: 'reload'
      }]
    })
  }