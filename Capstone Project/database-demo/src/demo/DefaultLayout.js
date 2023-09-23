import React, {useEffect} from 'react'
import { AppContent, AppSidebar, AppFooter, AppHeader } from './'

const DefaultLayout = () => {


  useEffect(() => {
    if(localStorage.getItem("loggedIn")===null) localStorage.setItem("loggedIn",false);
    const loggedIn=localStorage.getItem("loggedIn");
    if(loggedIn === 'false' ) window.location.href= "/#/login"
    console.log(loggedIn)
  }, []);
  return (
    <div>
      <AppSidebar />
      <div className="wrapper d-flex flex-column min-vh-100 bg-light">
        <AppHeader />
        <div className="body flex-grow-1 px-3">
          <AppContent />
        </div>
        <AppFooter />
      </div>
    </div>
  )
}

export default DefaultLayout
