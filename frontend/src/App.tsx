import React from "react";
import { Button } from "@/components/ui/button";
import { Calendar } from "@/components/ui/calendar";
import "./App.css";

function App() {
  const [date, setDate] = React.useState<Date | undefined>(new Date());

  return (
    <>
      <div>
        <h1 className="text-3xl font-bold underline">
          Tailwind CSS Hello world!
        </h1>
        <Button variant="outline">Button</Button>
        <Calendar mode="single" selected={date} onSelect={setDate} />
      </div>
    </>
  );
}

export default App;
