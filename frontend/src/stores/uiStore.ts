import { create } from 'zustand';

type ModalType = 'createProject' | 'createTask' | 'editProject' | 'editTask' | null;

interface UiState {
  isSidebarOpen: boolean;
  activeModal: ModalType;
  modalData: unknown;
  toggleSidebar: () => void;
  setSidebarOpen: (isOpen: boolean) => void;
  openModal: (modal: ModalType, data?: unknown) => void;
  closeModal: () => void;
}

export const useUiStore = create<UiState>((set) => ({
  isSidebarOpen: true,
  activeModal: null,
  modalData: null,

  toggleSidebar: () => set((state) => ({ isSidebarOpen: !state.isSidebarOpen })),
  
  setSidebarOpen: (isOpen: boolean) => set({ isSidebarOpen: isOpen }),

  openModal: (modal: ModalType, data?: unknown) => 
    set({ activeModal: modal, modalData: data }),

  closeModal: () => set({ activeModal: null, modalData: null }),
}));

export default useUiStore;

