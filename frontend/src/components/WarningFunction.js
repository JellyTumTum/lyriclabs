import { useEffect } from 'react';

const useWarnOnLeave = () => {
    useEffect(() => {
        const handleBeforeUnload = (e) => {
            const message = "Leaving disrupts the game for other users and yourself. Please don't unless necessary.";
            e.preventDefault();
            e.returnValue = message;
            return message; 
        };

        const handlePopState = () => {
            alert("Navigating away will disrupt the game. Please don't do it if it can be avoided.");
        };

        window.addEventListener("beforeunload", handleBeforeUnload); // For refreshing/closing window
        window.addEventListener("popstate", handlePopState); // For back and forward navigation

        // Cleanup function
        return () => {
            window.removeEventListener("beforeunload", handleBeforeUnload);
            window.removeEventListener("popstate", handlePopState);
        };
    }, []);

    return null; 
};

export default useWarnOnLeave;
