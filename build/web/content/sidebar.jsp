 <!--Start sidebar-wrapper-->
   <div id="sidebar-wrapper" data-simplebar="" data-simplebar-auto-hide="true">
     <div class="brand-logo">
      <a href="<%= request.getContextPath() %>/HomeServlet">
       <img src="assets/images/logo-icon.png" class="logo-icon" alt="logo icon">
       <h5 class="logo-text">JIRAMATY</h5>
     </a>
   </div>
   <ul class="sidebar-menu do-nicescrol">
      <li class="sidebar-header">Admin</li>
      <li>
        <a href="<%= request.getContextPath() %>/SearchServlet">
          <i class="zmdi zmdi-view-dashboard"></i> <span>Prevision</span>
        </a>
      </li>

    </ul>
   
   </div>
   <!--End sidebar-wrapper-->