import React from 'react';
import { NavLink } from 'react-router';
import { useUiStore } from '@/stores/uiStore';
import { ROUTES } from '@/utils/constants';
import {AnimatePresence, motion} from "framer-motion";

const navItems = [
  {
    name: 'Projects',
    path: ROUTES.PROJECTS,
    icon: (
      <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z" />
      </svg>
    ),
  },
];

export const Sidebar: React.FC = () => {
  const isSidebarOpen = useUiStore((state) => state.isSidebarOpen);

  const framerSidebar = {
    open: { width: '256px' },
    closed: { width: '0px' },
  };

  return (
      <motion.aside
        initial="closed"
        animate={isSidebarOpen ? "open" : "closed"}
        variants={framerSidebar}
        transition={{ duration: 0.5, ease: "easeInOut" }}
        className="w-64 bg-white border-r border-gray-200 min-h-screen">
      <nav className="p-4">
          <AnimatePresence mode="sync">
            <motion.ul className="space-y-2">
              {navItems.map((item) => (
                  <motion.li
                    layout
                    key={item.path}>
                      <motion.div
                          layout
                          initial={false}
                          animate={isSidebarOpen ? { opacity: 1, y: 0 } : { opacity: 0, y: -24 }}
                          exit={{ y: -24, opacity: 0 }}
                          transition={{ type: "spring", delay: 0.2 }}>
                          <NavLink
                            to={item.path}
                            className={({ isActive }) =>
                              `flex items-center gap-3 px-4 py-3 rounded-lg transition-colors ${
                                isActive
                                  ? 'bg-blue-50 text-blue-600 font-medium'
                                  : 'text-gray-700 hover:bg-gray-100'
                              }`
                            }
                          >
                            {item.icon}
                            {isSidebarOpen && <span>{item.name}</span>}
                          </NavLink>
                      </motion.div>
                  </motion.li>
              ))}
            </motion.ul>
          </AnimatePresence>
      </nav>
    </motion.aside>
  );
};

export default Sidebar;
